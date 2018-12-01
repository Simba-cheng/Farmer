package com.server.service.impl;

import com.server.bottom.ZooKeeperClient;
import com.server.constant.CommConstant;
import com.server.constant.ErrorMessageEnum;
import com.server.constant.NumberStrEnum;
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

    @Override
    public ResZKClientConectVO zkClientConect(String host) {

        ResZKClientConectVO zkClientConectVO = null;
        ResErrorInfo errorInfo;

        try {

            //check
            if (StringUtils.isEmpty(host)) {
                zkClientConectVO = new ResZKClientConectVO();
                zkClientConectVO.setIsSuccess(CommConstant.STRING_N);
                errorInfo = new ResErrorInfo(ErrorMessageEnum.ZK_Client_ERROR_01.getErrorCode(), ErrorMessageEnum.ZK_Client_ERROR_01.getErrorMessage());

                zkClientConectVO.setErrorInfo(errorInfo);
            }

            if (zooKeeperClient.isZkServerIsConn()) {
                zkClientConectVO = new ResZKClientConectVO();
                zkClientConectVO.setIsSuccess(CommConstant.STRING_N);
                errorInfo = new ResErrorInfo(ErrorMessageEnum.ZK_Client_ERROR_12.getErrorCode(), ErrorMessageEnum.ZK_Client_ERROR_12.getErrorMessage());
                zkClientConectVO.setErrorInfo(errorInfo);
            }

            //process
            if (null == zkClientConectVO) {

                //存储zkHost变量
                ZooKeeperController.ZK_HOST = host;

                LOGGER.info("===== 连接前，断开之前的连接 ======");
                zooKeeperClient.closeClientConn();
                LOGGER.info("===== 连接前，断开之前的连接 ======");

                zooKeeperClient.connect(host);
                zkClientConectVO = new ResZKClientConectVO(CommConstant.STRING_Y, null, "zookeeper连接成功");
            }

        } catch (Exception e) {
            LOGGER.error("host : {},error message : {}", new Object[]{host, e.getMessage()}, e);
            zkClientConectVO = new ResZKClientConectVO();
            zkClientConectVO.setIsSuccess(CommConstant.STRING_N);
            errorInfo = new ResErrorInfo(ErrorMessageEnum.ZK_Client_ERROR_02.getErrorCode(), ErrorMessageEnum.ZK_Client_ERROR_02.getErrorMessage());
            zkClientConectVO.setErrorInfo(errorInfo);
        }

        return zkClientConectVO;
    }

    @Override
    public ResCloseZKClientConnVO closeZKClientConn() {

        ResCloseZKClientConnVO closeZKClientConnVO;
        ResErrorInfo errorInfo;

        try {
            zooKeeperClient.closeClientConn();
            closeZKClientConnVO = new ResCloseZKClientConnVO(CommConstant.STRING_Y, null, "断开zookeeper连接成功");
        } catch (Exception e) {
            LOGGER.error("error message : {}", new Object[]{e.getMessage()}, e);
            closeZKClientConnVO = new ResCloseZKClientConnVO();
            closeZKClientConnVO.setIsSuccess(CommConstant.STRING_N);
            errorInfo = new ResErrorInfo(ErrorMessageEnum.ZK_Client_ERROR_03.getErrorCode(), ErrorMessageEnum.ZK_Client_ERROR_03.getErrorMessage());
            closeZKClientConnVO.setErrorInfo(errorInfo);
        }

        return closeZKClientConnVO;
    }

    @Override
    public ResCreateOneNodeVO createOneNode(String parentNode, String childNode, String nodeData, List<ACL> acl, CreateMode createMode) {

        ResCreateOneNodeVO createOneNodeVO = new ResCreateOneNodeVO();
        ResErrorInfo errorInfo;

        //check
        if (StringUtils.isEmpty(parentNode) || StringUtils.isEmpty(childNode)) {
            createOneNodeVO.setIsSuccess(CommConstant.STRING_N);
            errorInfo = new ResErrorInfo(ErrorMessageEnum.ZK_Client_ERROR_04.getErrorCode(), ErrorMessageEnum.ZK_Client_ERROR_04.getErrorMessage());
            createOneNodeVO.setErrorInfo(errorInfo);
            return createOneNodeVO;
        }

        if (CollectionUtils.isEmpty(acl)) {
            acl = ZooDefs.Ids.OPEN_ACL_UNSAFE;
        }

        if (null == createMode) {
            createMode = CreateMode.PERSISTENT;
        }

        if (StringUtils.isEmpty(nodeData)) {
            nodeData = "";
        }

        StringBuilder createNodePath = new StringBuilder();
        createNodePath.append(parentNode).append("/").append(childNode);
        String nodePath = createNodePath.toString().replace("_", "/").replace("-", ".").trim();

        //process
        try {
            String node = zooKeeperClient.createOneNode(nodePath, nodeData.getBytes(), acl, createMode);
            createOneNodeVO = new ResCreateOneNodeVO(CommConstant.STRING_Y, null, "创建节点成功", node);
        } catch (Exception e) {
            LOGGER.error("nodePath : {} , error message : {}", new Object[]{nodePath, e.getMessage()}, e);
            createOneNodeVO = new ResCreateOneNodeVO();
            createOneNodeVO.setIsSuccess(CommConstant.STRING_N);
            createOneNodeVO.setNodePath(nodePath);
            errorInfo = new ResErrorInfo(ErrorMessageEnum.ZK_Client_ERROR_05.getErrorCode(), ErrorMessageEnum.ZK_Client_ERROR_05.getErrorMessage());
            createOneNodeVO.setErrorInfo(errorInfo);

        }

        return createOneNodeVO;
    }

    @Override
    public ResCreateOneNodeVO createNodes(String nodePath, String data, List<ACL> acl, CreateMode createMode) {

        ResCreateOneNodeVO createOneNodeVO = null;

        //check
        if (StringUtils.isEmpty(nodePath)) {
            //createOneNodeVO = new ResCreateOneNodeVO(CommConstant.STRING_N, "ZK_Client_ERROR_04", "nodePath 节点为空", StringUtils.EMPTY, nodePath);
        }

        if (null == createOneNodeVO) {

            if (CollectionUtils.isEmpty(acl)) {
                acl = ZooDefs.Ids.OPEN_ACL_UNSAFE;
            }

            if (null == createMode) {
                createMode = CreateMode.PERSISTENT;
            }

            //process
            try {

                StringBuilder handleNode = new StringBuilder();

                boolean handleNodeResult = handleNode(nodePath, handleNode, data, acl, createMode);

                if (!handleNodeResult) {
                    //成功
                    createOneNodeVO = new ResCreateOneNodeVO();
                } else {
                    //失败
                    //createOneNodeVO = new ResCreateOneNodeVO(CommConstant.STRING_N, "ZK_Client_ERROR_08", "创建完整路径节点异常", StringUtils.EMPTY, nodePath);
                }

            } catch (Exception e) {
                LOGGER.error("创建完整路径节点 : {} 失败", new Object[]{nodePath});
                //createOneNodeVO = new ResCreateOneNodeVO(CommConstant.STRING_N, "ZK_Client_ERROR_08", "创建完整路径节点异常", StringUtils.EMPTY, nodePath);
            }
        }
        return createOneNodeVO;
    }

    /**
     * 递归循环节点，一层层判断、校验
     *
     * @param nodePath
     * @param handleNode
     */
    private boolean handleNode(String nodePath, StringBuilder handleNode, String data, List<ACL> acl, CreateMode createMode) throws Exception {

        handleNode.append("/");
        String tempData;

        //默认去掉第一个'/'
        nodePath = nodePath.substring(1, nodePath.length());

        //截取需要操作节点，不包含'/'则认为是最后一个节点
        if (nodePath.contains("/")) {
            handleNode.append(nodePath.substring(0, nodePath.indexOf("/")));
            tempData = "";
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

        if (nodePath.contains("/")) {
            //去掉已经操作的节点，截取下一次递归的后续节点
            String nextNode = nodePath.substring(nodePath.indexOf("/"), nodePath.length());
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
    private boolean handleNodePath(StringBuilder handleNode, String data, List<ACL> acl, CreateMode createMode) throws Exception {

        boolean handleNodeResult = true;

        //判断节点是否存在
        ResExitNodePathVO tempResultVO = exitNodePath(handleNode.toString());

        //if (null != tempResultVO && CommConstant.STRING_Y.equals(tempResultVO.getIsSuccess())) {
        //
        //    if (NumberStrEnum.ZERO_STR.getNumberStr().equals(tempResultVO.getIsExist())) {
        //
        //        LOGGER.info("节点 :{} 不存在 , 需要创建", new Object[]{handleNode});
        //
        //        //不存在,创建节点
        //        ResCreateOneNodeVO createOneNodeVO = createOneNode(handleNode.toString(), data, acl, createMode);
        //
        //        if (CommConstant.STRING_N.equals(createOneNodeVO.getIsSuccess())) {
        //            //创建某个节点失败，直接退出，不再继续往下创建
        //            handleNodeResult = false;
        //            LOGGER.info("节点 : {},创建失败", new Object[]{handleNode});
        //        } else if (CommConstant.STRING_Y.equals(createOneNodeVO.getIsSuccess())) {
        //            //创建节点成功
        //            handleNodeResult = true;
        //            LOGGER.info("节点 : {},创建成功", new Object[]{handleNode});
        //        }
        //
        //    } else {
        //        //节点存在
        //        LOGGER.info("节点 : {},已存在", new Object[]{handleNode});
        //    }
        //
        //} else {
        //    //判断节点是否存在接口操作异常，直接返回
        //    handleNodeResult = false;
        //    LOGGER.info("判断节点 : {} 是否存在，操作异常", new Object[]{handleNode});
        //}
        return handleNodeResult;
    }

    @Override
    public ResExitNodePathVO exitNodePath(String nodePath) {

        ResExitNodePathVO exitNodePathVO = new ResExitNodePathVO();
        String message;
        String isExist = "";
        try {
            Stat stat = zooKeeperClient.exitNodePath(nodePath);

            if (null == stat) {
                message = "节点不存在";
                isExist = "0";
            } else {
                message = "节点已存在";
                isExist = "1";
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
        ResErrorInfo errorInfo = null;

        //check
        if (StringUtils.isEmpty(nodePath)) {
            errorInfo = new ResErrorInfo(ErrorMessageEnum.ZK_Client_ERROR_04.getErrorCode(), ErrorMessageEnum.ZK_Client_ERROR_04.getErrorMessage());
            deleteNodeVO.setIsSuccess(CommConstant.STRING_N);
            deleteNodeVO.setErrorInfo(errorInfo);
            return deleteNodeVO;
        }

        try {
            nodePath = nodePath.replace("_", "/").replace("-", ".");

            zooKeeperClient.deleteNode(nodePath, version);
            deleteNodeVO = new ResDeleteNodeVO(CommConstant.STRING_Y, null, "删除节点成功", nodePath);
        } catch (Exception e) {
            LOGGER.error("nodePath : {} , error message : {}", new Object[]{nodePath, e.getMessage()}, e);
            errorInfo = new ResErrorInfo(ErrorMessageEnum.ZK_Client_ERROR_07.getErrorCode(), ErrorMessageEnum.ZK_Client_ERROR_07.getErrorMessage());
            deleteNodeVO.setIsSuccess(CommConstant.STRING_N);
            deleteNodeVO.setErrorInfo(errorInfo);
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
            Stat stat = zooKeeperClient.setDataForNodePath(nodeInfoDTO.getNodePath(), nodeInfoDTO.getNodeData().getBytes(), version);
            dataForNodeVO = new ResSetDataForNodeVO(CommConstant.STRING_Y, null, "插入数据成功", nodeInfoDTO.getNodePath(), nodeInfoDTO.getNodeData());

        } catch (Exception e) {
            LOGGER.error("更新数据异常 , nodePath : {} , data : {} , message : {}", new Object[]{nodeInfoDTO.getNodePath(), nodeInfoDTO.getNodeData(), e.getMessage()}, e);

            errorInfo = new ResErrorInfo(ErrorMessageEnum.ZK_Client_ERROR_10.getErrorCode(), ErrorMessageEnum.ZK_Client_ERROR_10.getErrorMessage());
            dataForNodeVO = new ResSetDataForNodeVO(CommConstant.STRING_N, errorInfo, StringUtils.EMPTY, nodeInfoDTO.getNodePath(), nodeInfoDTO.getNodeData());
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
                    resultData = "".getBytes();
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
                        nodeInfoVO.setCompleteNode(sb.toString().replace("/", "_").replace(".", "-").trim());
                        nodeInfoVO.setIsExistenceChild(existenceChildNode ? NumberStrEnum.ONE_STR.getNumberStr() : NumberStrEnum.ZERO_STR.getNumberStr());
                        nodeInfoVO.setNodeIsFile(childNode.contains(".") ? NumberStrEnum.ONE_STR.getNumberStr() : NumberStrEnum.ZERO_STR.getNumberStr());

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

        ResErrorInfo errorInfo = null;
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

            nodePath = nodePath.replace("_", "/").replace("-", ".");

            nodeInfoDTO.setNodePath(nodePath);
            nodeInfoDTO.setNodeData(StringUtils.isEmpty(reqNodeInfo.getNodeData()) ? "" : reqNodeInfo.getNodeData());

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

        if (NumberStrEnum.ONE_STR.getNumberStr().equals(result)) {
            errorInfo = new ResErrorInfo(ErrorMessageEnum.ZK_Client_ERROR_15.getErrorCode(), ErrorMessageEnum.ZK_Client_ERROR_15.getErrorMessage());
        } else if (NumberStrEnum.TWO_STR.getNumberStr().equals(result)) {
            errorInfo = new ResErrorInfo(ErrorMessageEnum.ZK_Client_ERROR_16.getErrorCode(), ErrorMessageEnum.ZK_Client_ERROR_16.getErrorMessage());
        }

        return errorInfo;
    }

    //@Override
    //public ResDeleteNodeVO deleteAllNodes(String nodePath, int version) {
    //
    //    ResDeleteNodeVO deleteNodeVO = new ResDeleteNodeVO();
    //    ResErrorInfo errorInfo;
    //
    //    if (StringUtils.isEmpty(nodePath)) {
    //        errorInfo = new ResErrorInfo(ErrorMessageEnum.ZK_Client_ERROR_04.getErrorCode(), ErrorMessageEnum.ZK_Client_ERROR_04.getErrorMessage());
    //        deleteNodeVO.setErrorInfo(errorInfo);
    //        deleteNodeVO.setIsSuccess(CommConstant.STRING_N);
    //        return deleteNodeVO;
    //    }
    //
    //    try {
    //
    //        String deleteNodePath = nodePath.replace("_", "/").replace("-", ".").trim();
    //
    //        int length = StringUtils.countMatches(deleteNodePath, "/");
    //
    //        for (int i = 0; i < length; i++) {
    //
    //            String node;
    //
    //            if (i == 0) {
    //                //第一次
    //                node = deleteNodePath;
    //            } else {
    //                //开始截取
    //                node = deleteNodePath.substring(0, deleteNodePath.lastIndexOf("/"));
    //                deleteNodePath = node;
    //            }
    //
    //            if (StringUtils.isEmpty(node)) {
    //                continue;
    //            }
    //
    //            zooKeeperClient.deleteNode(node, version);
    //        }
    //
    //    } catch (Exception e) {
    //        LOGGER.error("nodePath : {} , error message : {}", new Object[]{nodePath, e.getMessage()}, e);
    //
    //        errorInfo = new ResErrorInfo(ErrorMessageEnum.ZK_Client_ERROR_07.getErrorCode(), ErrorMessageEnum.ZK_Client_ERROR_07.getErrorMessage());
    //        deleteNodeVO.setErrorInfo(errorInfo);
    //        deleteNodeVO.setIsSuccess(CommConstant.STRING_N);
    //    }
    //
    //    return deleteNodeVO;
    //}
}
