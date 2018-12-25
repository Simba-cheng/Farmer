package com.server.service.impl;

import com.server.bottom.ZooKeeperClient;
import com.server.constant.CommConstant;
import com.server.constant.ErrorMessageEnum;
import com.server.constant.NumberEnum;
import com.server.controller.zk.ZooKeeperController;
import com.server.dto.NodeInfoDTO;
import com.server.service.ZooKeeperClientService;
import com.server.util.JsonUtils;
import com.server.vo.request.ReqNodeInfo;
import com.server.vo.response.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author CYX
 * @date 2018/11/11 11:35
 */
@Service
public class ZooKeeperClientServiceImpl implements ZooKeeperClientService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZooKeeperClientServiceImpl.class);

    /**
     * ZK节点CRUD 底层客户端
     */
    @Autowired
    private ZooKeeperClient zooKeeperClient;

    /**
     * 删除所有子节点-临时存储节点路径
     */
    private List<String> temp_deleteAllChildNodes = new ArrayList<String>();

    /**
     * 删除所有子节点-最终所有需要删除的节点路径
     */
    private LinkedList<String> result_deleteAllChildNodes = new LinkedList<String>();

    @Override
    public ResZKClientConectVO zkClientConect(String host) {

        ResZKClientConectVO zkClientConectVO = new ResZKClientConectVO();
        //校验成功/失败标示
        boolean checkFlag = true;

        try {

            //check
            if (StringUtils.isEmpty(host)) {
                zkClientConectVO.setIsSuccess(CommConstant.STRING_N);
                zkClientConectVO.setErrorInfo(new ResErrorInfo(ErrorMessageEnum.ZK_Client_ERROR_01.getErrorCode(), ErrorMessageEnum.ZK_Client_ERROR_01.getErrorMessage()));
                checkFlag = false;
            }

            //zookeeper服务端是否已经连接
            if (zooKeeperClient.isZkServerIsConn()) {
                zkClientConectVO.setIsSuccess(CommConstant.STRING_N);
                zkClientConectVO.setErrorInfo(new ResErrorInfo(ErrorMessageEnum.ZK_Client_ERROR_12.getErrorCode(), ErrorMessageEnum.ZK_Client_ERROR_12.getErrorMessage()));
                checkFlag = false;
            }

            //process
            if (checkFlag) {

                //存储zkHost变量
                ZooKeeperController.ZK_HOST = host;

                /*
                    上面判断'zookeeper服务端是否已经连接',这边又'断开之前的连接'，这边是这样考虑的：

                    用户在页面输入了一个错误的ZooKeeper Host信息，ZooKeeper底层客户端会一直尝试连接，
                    页面jquery采用了异步超时处理，超过10秒没有连接上，则认为用户输入了错误的host信息，会提示用户，重新连接。
                    同时，isZkServerIsConn()标志位一直是false，用户再次输入正确的host信息，这里需要把之前未断开的错误连接断开。

                    但如果用户在已经连接zk服务器的情况下，重复连接，就会被isZkServerIsConn()方法拦截下来。
                 */
                LOGGER.info("===== 连接前，断开之前的连接 ======");
                zooKeeperClient.closeClientConn();
                LOGGER.info("===== 连接前，断开之前的连接 ======");

                zooKeeperClient.connect(host);

                zkClientConectVO.setIsSuccess(CommConstant.STRING_Y);
                zkClientConectVO.setDisplayCopy("zookeeper连接成功");
            }

        } catch (Exception e) {
            LOGGER.error("host : {},error message : {}", new Object[]{host, e.getMessage()}, e);
            zkClientConectVO.setIsSuccess(CommConstant.STRING_N);
            zkClientConectVO.setErrorInfo(new ResErrorInfo(ErrorMessageEnum.ZK_Client_ERROR_02.getErrorCode(), ErrorMessageEnum.ZK_Client_ERROR_02.getErrorMessage()));
        }

        return zkClientConectVO;
    }

    @Override
    public ResCloseZKClientConnVO closeZKClientConn() {

        ResCloseZKClientConnVO closeZKClientConnVO = new ResCloseZKClientConnVO();

        try {
            zooKeeperClient.closeClientConn();

            closeZKClientConnVO.setIsSuccess(CommConstant.STRING_Y);
            closeZKClientConnVO.setDisplayCopy("断开zookeeper连接成功");

        } catch (Exception e) {
            LOGGER.error("error message : {}", new Object[]{e.getMessage()}, e);
            closeZKClientConnVO.setIsSuccess(CommConstant.STRING_N);
            closeZKClientConnVO.setErrorInfo(new ResErrorInfo(ErrorMessageEnum.ZK_Client_ERROR_03.getErrorCode(), ErrorMessageEnum.ZK_Client_ERROR_03.getErrorMessage()));
        }

        return closeZKClientConnVO;
    }

    @Override
    public ResCreateOneNodeVO createOneNode(String parentNode, String childNode, String nodeData, List<ACL> acl, CreateMode createMode) {

        ResCreateOneNodeVO createOneNodeVO = new ResCreateOneNodeVO();

        //check
        if (StringUtils.isEmpty(parentNode) || StringUtils.isEmpty(childNode)) {
            createOneNodeVO.setIsSuccess(CommConstant.STRING_N);
            createOneNodeVO.setErrorInfo(new ResErrorInfo(ErrorMessageEnum.ZK_Client_ERROR_04.getErrorCode(), ErrorMessageEnum.ZK_Client_ERROR_04.getErrorMessage()));
            return createOneNodeVO;
        }

        if (CollectionUtils.isEmpty(acl)) {
            acl = ZooDefs.Ids.OPEN_ACL_UNSAFE;
        }

        if (null == createMode) {
            createMode = CreateMode.PERSISTENT;
        }

        if (StringUtils.isEmpty(nodeData)) {
            nodeData = StringUtils.EMPTY;
        }

        StringBuilder createNodePath = new StringBuilder();
        createNodePath.append(parentNode).append(CommConstant.SLASH).append(childNode);
        String nodePath = createNodePath.toString();

        //process
        try {
            String node = zooKeeperClient.createOneNode(nodePath, nodeData.getBytes(), acl, createMode);

            createOneNodeVO.setIsSuccess(CommConstant.STRING_Y);
            createOneNodeVO.setDisplayCopy("创建节点成功");
            createOneNodeVO.setNodePath(node);
        } catch (Exception e) {
            LOGGER.error("nodePath : {} , error message : {}", new Object[]{nodePath, e.getMessage()}, e);
            createOneNodeVO.setIsSuccess(CommConstant.STRING_N);
            createOneNodeVO.setNodePath(nodePath);
            createOneNodeVO.setErrorInfo(new ResErrorInfo(ErrorMessageEnum.ZK_Client_ERROR_05.getErrorCode(), ErrorMessageEnum.ZK_Client_ERROR_05.getErrorMessage()));
        }

        return createOneNodeVO;
    }

    @Override
    public ResCreateAllNodeVO createNodes(String nodePath, String data, List<ACL> acl, CreateMode createMode) {

        ResCreateAllNodeVO resCreateAllNodeVO = new ResCreateAllNodeVO();

        //校验成功/失败标示
        boolean checkFlag = true;

        //check
        if (StringUtils.isEmpty(nodePath)) {
            resCreateAllNodeVO.setIsSuccess(CommConstant.STRING_N);
            resCreateAllNodeVO.setErrorInfo(new ResErrorInfo(ErrorMessageEnum.ZK_Client_ERROR_04.getErrorCode(), ErrorMessageEnum.ZK_Client_ERROR_04.getErrorMessage()));
            checkFlag = false;
        }

        if (checkFlag) {

            if (CollectionUtils.isEmpty(acl)) {
                acl = ZooDefs.Ids.OPEN_ACL_UNSAFE;
            }

            if (null == createMode) {
                createMode = CreateMode.PERSISTENT;
            }

            //process
            try {

                StringBuilder handleNode = new StringBuilder();

                //递归循环节点，一层层判断、校验
                boolean handleNodeResult = handleNode(nodePath, handleNode, data, acl, createMode);

                if (handleNodeResult) {
                    //成功
                    resCreateAllNodeVO.setIsSuccess(CommConstant.STRING_Y);
                    resCreateAllNodeVO.setDisplayCopy("节点创建成功");
                    resCreateAllNodeVO.setNodePath(nodePath);
                } else {
                    //失败
                    resCreateAllNodeVO.setIsSuccess(CommConstant.STRING_N);
                    resCreateAllNodeVO.setErrorInfo(new ResErrorInfo(ErrorMessageEnum.ZK_Client_ERROR_08.getErrorCode(), ErrorMessageEnum.ZK_Client_ERROR_08.getErrorMessage()));
                }

            } catch (Exception e) {
                LOGGER.error("创建完整路径节点 : {} 失败", new Object[]{nodePath}, e);
                resCreateAllNodeVO.setIsSuccess(CommConstant.STRING_N);
                resCreateAllNodeVO.setErrorInfo(new ResErrorInfo(ErrorMessageEnum.ZK_Client_ERROR_08.getErrorCode(), ErrorMessageEnum.ZK_Client_ERROR_08.getErrorMessage()));
            }
        }

        return resCreateAllNodeVO;
    }

    /**
     * 递归循环节点，一层层判断、校验
     *
     * @param nodePath
     * @param handleNode
     * @param data
     * @param acl
     * @param createMode
     * @return
     * @throws Exception
     */
    private boolean handleNode(String nodePath, StringBuilder handleNode, String data, List<ACL> acl, CreateMode createMode) throws Exception {

        handleNode.append(CommConstant.SLASH);
        String tempData;

        //默认去掉第一个'/'
        nodePath = nodePath.substring(1, nodePath.length());

        //截取需要操作节点，不包含'/'则认为是最后一个节点
        if (nodePath.contains(CommConstant.SLASH)) {
            handleNode.append(nodePath.substring(0, nodePath.indexOf(CommConstant.SLASH)));
            tempData = StringUtils.EMPTY;
        } else {
            handleNode.append(nodePath);
            tempData = data;
        }

        LOGGER.info("准备操作的节点 : {}", new Object[]{handleNode.toString()});

        //process 操作节点
        boolean handleNodeResult = handleNodePath(handleNode, tempData, acl, createMode);
        if (!handleNodeResult) {
            //操作节点异常,直接退出,不在递归
            return false;
        }

        if (nodePath.contains(CommConstant.SLASH)) {
            //去掉已经操作的节点，截取下一次递归的后续节点
            String nextNode = nodePath.substring(nodePath.indexOf(CommConstant.SLASH), nodePath.length());
            LOGGER.info("递归继续操作的节点 : {}", new Object[]{nextNode});

            handleNode(nextNode, handleNode, data, acl, createMode);
        } else {
            //不包含'/'，认为已是最后一个节点
            return true;
        }
        return true;
    }

    /**
     * 判断节点是否存在
     * 不存在：创建
     * 存在：跳过/不管
     *
     * @param handleNode
     * @param data
     * @param acl
     * @param createMode
     * @return false-操作失败 true-操作成功
     * @throws Exception
     */
    private boolean handleNodePath(StringBuilder handleNode, String data, List<ACL> acl, CreateMode createMode) {

        boolean handleNodeResult = true;

        try {
            //判断节点是否存在
            Stat stat = zooKeeperClient.exitNodePath(handleNode.toString());

            if (null == stat) {
                //不存在,创建节点
                LOGGER.info("节点 :{} 不存在 , 需要创建", new Object[]{handleNode});

                zooKeeperClient.createOneNode(handleNode.toString(), data.getBytes(), acl, createMode);
            } else {
                //节点存在
                LOGGER.info("节点 : {},已存在", new Object[]{handleNode});
            }
        } catch (Exception e) {
            LOGGER.error("创建节点失败,nodePath : {}", new Object[]{handleNode}, e);
            handleNodeResult = false;
        }

        return handleNodeResult;
    }


    @Override
    public ResExitNodePathVO exitNodePath(String nodePath) {

        ResExitNodePathVO exitNodePathVO = new ResExitNodePathVO();
        String message;
        String isExist;
        try {
            Stat stat = zooKeeperClient.exitNodePath(nodePath);

            if (null == stat) {
                message = "节点不存在";
                isExist = NumberEnum.ZERO_STR.getNumberStr();
            } else {
                message = "节点已存在";
                isExist = NumberEnum.ONE_STR.getNumberStr();
            }
            //exitNodePathVO = new ResExitNodePathVO(CommConstant.STRING_Y, StringUtils.EMPTY, StringUtils.EMPTY, message, nodePath, isExist);
        } catch (Exception e) {
            LOGGER.error("nodePath : {} , error message : {}", new Object[]{nodePath, e.getMessage()}, e);
            //exitNodePathVO = new ResExitNodePathVO(CommConstant.STRING_N, "ZK_Client_ERROR_06", "判断节点是否存在操作异常", StringUtils.EMPTY, nodePath, isExist);
        }

        return exitNodePathVO;
    }

    @Override
    public ResDeleteNodeVO deleteNode(String nodePath, int version) {

        ResDeleteNodeVO deleteNodeVO = new ResDeleteNodeVO();
        String resultNodePath = StringUtils.EMPTY;

        //check
        if (StringUtils.isEmpty(nodePath)) {
            deleteNodeVO.setIsSuccess(CommConstant.STRING_N);
            deleteNodeVO.setErrorInfo(new ResErrorInfo(ErrorMessageEnum.ZK_Client_ERROR_04.getErrorCode(), ErrorMessageEnum.ZK_Client_ERROR_04.getErrorMessage()));
            return deleteNodeVO;
        }

        try {
            zooKeeperClient.deleteNode(nodePath, version);

            deleteNodeVO.setIsSuccess(CommConstant.STRING_Y);
            deleteNodeVO.setDisplayCopy("删除节点成功");
            deleteNodeVO.setNodePath(resultNodePath);

        } catch (Exception e) {
            LOGGER.error("nodePath : {} , error message : {}", new Object[]{resultNodePath, e.getMessage()}, e);
            deleteNodeVO.setIsSuccess(CommConstant.STRING_N);
            deleteNodeVO.setErrorInfo(new ResErrorInfo(ErrorMessageEnum.ZK_Client_ERROR_07.getErrorCode(), ErrorMessageEnum.ZK_Client_ERROR_07.getErrorMessage()));
        }

        return deleteNodeVO;
    }

    @Override
    public ResSetDataForNodeVO setDataForNodePath(String inputData, int version) {

        ResSetDataForNodeVO dataForNodeVO = new ResSetDataForNodeVO();
        NodeInfoDTO nodeInfoDTO = new NodeInfoDTO();
        ResErrorInfo errorInfo;
        try {

            //check
            errorInfo = checkInputParam(inputData, nodeInfoDTO);
            if (null != errorInfo) {
                dataForNodeVO.setErrorInfo(errorInfo);
                dataForNodeVO.setIsSuccess(CommConstant.STRING_N);
                return dataForNodeVO;
            }

            if (StringUtils.isEmpty(nodeInfoDTO.getNodeData())) {
                errorInfo = new ResErrorInfo(ErrorMessageEnum.ZK_Client_ERROR_09.getErrorCode(), ErrorMessageEnum.ZK_Client_ERROR_09.getErrorMessage());
                dataForNodeVO.setErrorInfo(errorInfo);
                dataForNodeVO.setIsSuccess(CommConstant.STRING_N);
                return dataForNodeVO;
            }

            //process
            zooKeeperClient.setDataForNodePath(nodeInfoDTO.getNodePath(), nodeInfoDTO.getNodeData().getBytes(), version);
            dataForNodeVO.setIsSuccess(CommConstant.STRING_Y);
            dataForNodeVO.setNodePath(nodeInfoDTO.getNodePath());
            dataForNodeVO.setDisplayCopy("插入数据成功");
            dataForNodeVO.setData(nodeInfoDTO.getNodeData());

        } catch (Exception e) {
            LOGGER.error("更新数据异常 , nodePath : {} , data : {} , message : {}", new Object[]{nodeInfoDTO.getNodePath(), nodeInfoDTO.getNodeData(), e.getMessage()}, e);
            dataForNodeVO.setIsSuccess(CommConstant.STRING_N);
            dataForNodeVO.setErrorInfo(new ResErrorInfo(ErrorMessageEnum.ZK_Client_ERROR_10.getErrorCode(), ErrorMessageEnum.ZK_Client_ERROR_10.getErrorMessage()));
            dataForNodeVO.setNodePath(nodeInfoDTO.getNodePath());
            dataForNodeVO.setData(nodeInfoDTO.getNodeData());
        }

        return dataForNodeVO;
    }

    @Override
    public ResGetDataForNodeVO getDataForNodePath(String inputData, Watcher watcher, Stat stat) {

        ResGetDataForNodeVO getDataForNodeVO = new ResGetDataForNodeVO();
        NodeInfoDTO nodeInfoDTO = new NodeInfoDTO();
        ResErrorInfo errorInfo;
        String nodePath = null;

        try {

            errorInfo = checkInputParam(inputData, nodeInfoDTO);
            nodePath = nodeInfoDTO.getNodePath();

            if (null == errorInfo && StringUtils.isNotEmpty(nodePath)) {

                byte[] resultData = zooKeeperClient.getDataForNodePath(nodePath, watcher, stat);

                if (null == resultData) {
                    resultData = StringUtils.EMPTY.getBytes();
                }

                String data = IOUtils.toString(resultData, CommConstant.CODING_UTF8);
                getDataForNodeVO = new ResGetDataForNodeVO(CommConstant.STRING_Y, null, "获取节点中数据成功", nodePath, data);

            } else {
                getDataForNodeVO.setErrorInfo(errorInfo);
                getDataForNodeVO.setIsSuccess(CommConstant.STRING_N);
            }

        } catch (Exception e) {
            LOGGER.error("nodePath : {} , 获取节点中数据异常", new Object[]{nodePath});
            errorInfo = new ResErrorInfo(ErrorMessageEnum.ZK_Client_ERROR_11.getErrorCode(), ErrorMessageEnum.ZK_Client_ERROR_11.getErrorMessage());
            getDataForNodeVO = new ResGetDataForNodeVO(CommConstant.STRING_N, errorInfo, StringUtils.EMPTY, nodePath, StringUtils.EMPTY);
        }

        return getDataForNodeVO;
    }

    @Override
    public ResZKClientResultVO getDataForNodePathWithFile(String fileName, String nodePath, Watcher watcher, Stat stat) {
        return null;
    }

    @Override
    public ResZKClientResultVO createFileToNode(String fileName, String nodePath, String data, List<ACL> acl, CreateMode createMode) {
        return null;
    }

    @Override
    public ResGetChildNodeVO getChildNode(String inputData, Watcher watcher) {

        ResGetChildNodeVO getChildNodeVO = new ResGetChildNodeVO();
        NodeInfoDTO nodeInfoDTO = new NodeInfoDTO();
        List<ResNodeInfoVO> resNodeInfoVOS = new ArrayList<ResNodeInfoVO>();
        List<String> childNodes;
        ResErrorInfo errorInfo;
        String nodePath = null;

        try {

            //check
            errorInfo = checkInputParam(inputData, nodeInfoDTO);

            if (null == errorInfo && StringUtils.isNotEmpty(nodeInfoDTO.getNodePath())) {

                nodePath = nodeInfoDTO.getNodePath();

                //查询子节点
                childNodes = zooKeeperClient.getChildNodePath(nodePath, watcher);

                //判断子节点下是否还有子节点 && 封装对象
                if (CollectionUtils.isNotEmpty(childNodes)) {

                    StringBuilder sb;

                    for (String childNode : childNodes) {
                        if (null == childNode) {
                            continue;
                        }

                        //拼接二次查询的节点
                        sb = new StringBuilder();
                        sb.append(nodePath);
                        //放置根节点查询，出现两个'//'斜杠
                        if (!CommConstant.SLASH.equals(nodePath)) {
                            sb.append(CommConstant.SLASH);
                        }
                        sb.append(childNode);

                        //是否还有子节点
                        boolean existenceChildNode = isExistenceChildNode(sb.toString());

                        //构建对象
                        ResNodeInfoVO nodeInfoVO = new ResNodeInfoVO();
                        nodeInfoVO.setNodePath(childNode.trim());
                        nodeInfoVO.setCompleteNode(sb.toString().trim());
                        nodeInfoVO.setIsExistenceChild(existenceChildNode ? NumberEnum.ONE_STR.getNumberStr() : NumberEnum.ZERO_STR.getNumberStr());
                        nodeInfoVO.setNodeIsFile(childNode.contains(CommConstant.POINT) ? NumberEnum.ONE_STR.getNumberStr() : NumberEnum.ZERO_STR.getNumberStr());

                        resNodeInfoVOS.add(nodeInfoVO);
                    }
                }

                getChildNodeVO = new ResGetChildNodeVO(CommConstant.STRING_Y, null, "查询子节点成功", nodePath, resNodeInfoVOS);

            } else {
                getChildNodeVO.setErrorInfo(errorInfo);
                getChildNodeVO.setIsSuccess(CommConstant.STRING_N);
            }

        } catch (Exception e) {
            LOGGER.error("nodePath : {} , error message : {}", new Object[]{nodePath, e.getMessage()}, e);
            errorInfo = new ResErrorInfo(ErrorMessageEnum.ZK_Client_ERROR_13.getErrorCode(), ErrorMessageEnum.ZK_Client_ERROR_13.getErrorMessage());
            getChildNodeVO = new ResGetChildNodeVO(CommConstant.STRING_N, errorInfo, StringUtils.EMPTY, nodePath, null);
        }
        return getChildNodeVO;
    }

    @Override
    public boolean isExistenceChildNode(String nodePath) {

        //默认存在
        boolean result = true;

        try {
            List<String> childNodePaths = zooKeeperClient.getChildNodePath(nodePath, null);
            if (CollectionUtils.isEmpty(childNodePaths)) {
                result = false;
            }
        } catch (Exception e) {
            LOGGER.error("nodePath : {} , error message : {}", new Object[]{nodePath, e.getMessage()}, e);
            result = false;
        }

        return result;
    }

    /**
     * 入参校验
     *
     * @param inputData
     * @param nodeInfoDTO
     * @return
     */
    private ResErrorInfo checkInputParam(String inputData, NodeInfoDTO nodeInfoDTO) {

        ResErrorInfo errorInfo;
        String nodePath;
        try {

            //判断连接是否异常
            errorInfo = whetherToConnect();
            if (null != errorInfo) {
                return errorInfo;
            }

            if (StringUtils.isEmpty(inputData)) {
                errorInfo = new ResErrorInfo(ErrorMessageEnum.ZK_Client_ERROR_00.getErrorCode(), ErrorMessageEnum.ZK_Client_ERROR_00.getErrorMessage());
            }

            ReqNodeInfo reqNodeInfo = JsonUtils.toJavaObject(inputData, ReqNodeInfo.class);
            nodePath = reqNodeInfo.getNodePath();
            if (StringUtils.isEmpty(nodePath)) {
                errorInfo = new ResErrorInfo(ErrorMessageEnum.ZK_Client_ERROR_04.getErrorCode(), ErrorMessageEnum.ZK_Client_ERROR_04.getErrorMessage());
            }

            nodeInfoDTO.setNodePath(nodePath);
            nodeInfoDTO.setNodeData(StringUtils.isEmpty(reqNodeInfo.getNodeData()) ? StringUtils.EMPTY : reqNodeInfo.getNodeData());

        } catch (Exception e) {
            LOGGER.error("inputData : {} , error message : {}", new Object[]{inputData, e.getMessage()}, e);
            errorInfo = new ResErrorInfo(ErrorMessageEnum.ZK_Client_ERROR_14.getErrorCode(), ErrorMessageEnum.ZK_Client_ERROR_14.getErrorMessage());
        }

        return errorInfo;
    }

    @Override
    public ResErrorInfo whetherToConnect() {

        ResErrorInfo errorInfo = null;

        //0-连接正常，1-未连接，2-连接超时或其他连接异常
        String result = zooKeeperClient.isConn();

        if (NumberEnum.ONE_STR.getNumberStr().equals(result)) {
            errorInfo = new ResErrorInfo(ErrorMessageEnum.ZK_Client_ERROR_15.getErrorCode(), ErrorMessageEnum.ZK_Client_ERROR_15.getErrorMessage());
        } else if (NumberEnum.TWO_STR.getNumberStr().equals(result)) {
            errorInfo = new ResErrorInfo(ErrorMessageEnum.ZK_Client_ERROR_16.getErrorCode(), ErrorMessageEnum.ZK_Client_ERROR_16.getErrorMessage());
        }

        return errorInfo;
    }

    @Override
    public ResDeleteNodeVO deleteAllNodes(String nodePath, int version) {

        ResDeleteNodeVO deleteNodeVO = new ResDeleteNodeVO();
        ResErrorInfo errorInfo;
        String deleteNode = "";

        if (StringUtils.isEmpty(nodePath)) {
            errorInfo = new ResErrorInfo(ErrorMessageEnum.ZK_Client_ERROR_04.getErrorCode(), ErrorMessageEnum.ZK_Client_ERROR_04.getErrorMessage());
            deleteNodeVO.setErrorInfo(errorInfo);
            deleteNodeVO.setIsSuccess(CommConstant.STRING_N);
            return deleteNodeVO;
        }

        // process
        // 找出当前节点下，所有的子节点，然后循环删除
        try {

            //解析
            String currentNodePath = nodePath;

            //递归查询所有子节点
            recursiveQueryAllChildNode(currentNodePath);
            LOGGER.info("result_deleteAllChildNodes : {}", new Object[]{result_deleteAllChildNodes});

            //delete
            int deleteNodeSize = result_deleteAllChildNodes.size();
            for (int i = 0; i < deleteNodeSize; i++) {

                // 从最后一个节点开始删除
                deleteNode = result_deleteAllChildNodes.pollLast();

                zooKeeperClient.deleteNode(deleteNode, -1);
            }

            deleteNodeVO = new ResDeleteNodeVO(CommConstant.STRING_Y, null, "节点:" + currentNodePath + "下所有节点 删除成功", currentNodePath);

        } catch (Exception e) {
            LOGGER.error("nodePath : {} , error message : {}", new Object[]{nodePath, e.getMessage()}, e);

            errorInfo = new ResErrorInfo(ErrorMessageEnum.ZK_Client_ERROR_17.getErrorCode(), ErrorMessageEnum.ZK_Client_ERROR_17.getErrorMessage());
            deleteNodeVO.setErrorInfo(errorInfo);
            deleteNodeVO.setIsSuccess(CommConstant.STRING_N);
            deleteNodeVO.setNodePath(deleteNode);
        }

        return deleteNodeVO;
    }

    /**
     * 递归查询节点下所有子节点
     *
     * @param currentNodePath 当前节点
     */
    private void recursiveQueryAllChildNode(String currentNodePath) throws Exception {

        temp_deleteAllChildNodes.clear();
        result_deleteAllChildNodes.clear();

        temp_deleteAllChildNodes.add(currentNodePath);
        result_deleteAllChildNodes.add(currentNodePath);

        recursiveQueryChildNode();

    }

    /**
     * 递归查询子节点
     */
    private void recursiveQueryChildNode() throws Exception {

        if (CollectionUtils.isNotEmpty(temp_deleteAllChildNodes)) {

            List<String> tempNode = new ArrayList<String>();

            Iterator<String> nodeIterator = temp_deleteAllChildNodes.iterator();

            while (nodeIterator.hasNext()) {

                String node = nodeIterator.next();

                //在迭代中删除已经获取的节点
                nodeIterator.remove();

                //查询子节点
                List<String> allChildNodePath = zooKeeperClient.getChildNodePath(node, null);

                if (CollectionUtils.isNotEmpty(allChildNodePath)) {

                    List<String> allChildNodes = assembleNodePath(node, allChildNodePath);

                    tempNode.addAll(allChildNodes);

                } else {
                    continue;
                }
            }

            temp_deleteAllChildNodes.addAll(tempNode);
            //所有查出来的节点存入集合，最后循环删除
            result_deleteAllChildNodes.addAll(tempNode);

            //递归
            recursiveQueryChildNode();
        } else {
            return;
        }
    }

    /**
     * 循环拼装子节点路径
     *
     * @param rootNode  父节点路径
     * @param childNode 子节点集合
     * @return
     */
    private List<String> assembleNodePath(String rootNode, List<String> childNode) throws Exception {

        List<String> allChildNodePaths = new ArrayList<String>();

        for (String childNodePath : childNode) {

            String childPath = rootNode + CommConstant.SLASH + childNodePath;

            allChildNodePaths.add(childPath);
        }

        return allChildNodePaths;
    }

}
