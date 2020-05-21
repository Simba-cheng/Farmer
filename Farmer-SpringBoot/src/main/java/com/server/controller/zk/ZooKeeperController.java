package com.server.controller.zk;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.server.constant.CommConstant;
import com.server.constant.NumberEnum;
import com.server.controller.AbstractControllerComm;
import com.server.service.ZooKeeperClientService;
import com.server.util.PubUtils;
import com.server.vo.response.*;
import org.apache.commons.io.IOUtils;
import org.apache.zookeeper.ZooDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static org.apache.zookeeper.CreateMode.PERSISTENT;

/**
 * @author CYX
 * @date 2018/11/10 17:10
 */
@Controller
@RequestMapping(value = "/zk")
public class ZooKeeperController extends AbstractControllerComm {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZooKeeperController.class);

    @Resource
    private ZooKeeperClientService zkClientService;

    /**
     * 用来存储ZooKeeper服务端host配置
     */
    public static String ZK_HOST = "";

    public static Gson gson = (new GsonBuilder()).enableComplexMapKeySerialization().create();

    /**
     * 首页展示
     *
     * @param modelAndView
     * @param request
     * @param response
     * @return
     */
    @GetMapping(value = "/index.do")
    public ModelAndView zkIndex(ModelAndView modelAndView, HttpServletRequest request, HttpServletResponse response) {

        LOGGER.info("===== index 展示首页 =====");

        modelAndView.setViewName("zkIndex");

        return modelAndView;
    }

    /**
     * 链接zk
     *
     * @param host
     * @param request
     * @param response
     */
    @PostMapping(value = "/conn.do")
    public void zkClientConect(String host, HttpServletRequest request, HttpServletResponse response) {

        LOGGER.info("===== 连接ZooKeeper服务器 =====");
        LOGGER.info("zookeeper host : {}", new Object[]{host});

        ResultVO resultVO = new ResultVO();
        resultVO.setIsSuccess(NumberEnum.ONE_STR.getNumberStr());

        ResZKClientConectVO resZKClientConectVO = zkClientService.zkClientConect(host);
        resultVO.setResultData(resZKClientConectVO);

        String resultJson = gson.toJson(resultVO);

        LOGGER.info("result info : {}", new Object[]{resultJson});

        flushResultToPage(response, resultJson);
    }

    /**
     * 关闭zk client 链接
     *
     * @param request
     * @param response
     */
    //@GetMapping(value = "/close.do")
    //public void zkClientClose(HttpServletRequest request, HttpServletResponse response) {
    //
    //    LOGGER.info("===== 关闭ZooKeeper服务端的连接 =====");
    //
    //    ResultVO resultVO = new ResultVO();
    //    resultVO.setIsSuccess(NumberEnum.ONE_STR.getNumberStr());
    //
    //    ResCloseZKClientConnVO resCloseZKClientConnVO = zkClientService.closeZKClientConn();
    //    resultVO.setResultData(resCloseZKClientConnVO);
    //
    //    String resultJson = gson.toJson(resultVO);
    //    LOGGER.info("result info : {}", new Object[]{resultJson});
    //
    //    flushResultToPage(response, resultJson);
    //}

    /**
     * index页面初始化时，查询根节点下第一层节点
     * <p>
     * 注意：只展示一层
     * <p>
     * 第一层查询完成之后，会继续查询第二层，以此判断第一层节点是否还存在子节点。
     * 这个判断用作前台页面图标展示。
     * <p>
     * 不过点击(打开)节点的时候(包括末尾节点)，依旧会查询子节点。
     * 因为担心上面的判断异常，这是一个补偿机制。
     *
     * @param request
     * @param response
     */
    @GetMapping(value = "/indexQuery.do")
    public void indexQuery(String inputData, HttpServletRequest request, HttpServletResponse response) {

        LOGGER.info("===== index页面初始化,初始查询 =====");
        LOGGER.info("inputData : {}", new Object[]{inputData});

        Map<String, Object> jsonMap = new HashMap<String, Object>();
        ResultVO resultVO = new ResultVO();

        resultVO.setIsSuccess(NumberEnum.ONE_STR.getNumberStr());
        ResGetChildNodeVO childNodeVO = zkClientService.getChildNode(inputData, null);

        if (null == childNodeVO.getErrorInfo()) {
            resultVO.setResultData(childNodeVO);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("resultVO", resultVO);

            String htmlData = getOutputHtmlStr(map, "nodeInfo.ftl");
            jsonMap.put("html", htmlData);
            jsonMap.put("code", "Y");
        } else {
            jsonMap.put("html", "");
            jsonMap.put("code", "N");
            jsonMap.put("errorCode", childNodeVO.getErrorInfo().getErrorCode());
            jsonMap.put("errorMessage", childNodeVO.getErrorInfo().getErrorMessage());
        }


        String resultJSON = gson.toJson(jsonMap);
        LOGGER.info("result info : {}", new Object[]{resultJSON});

        flushResultToPage(response, resultJSON);

    }

    /**
     * 查询节点信息
     *
     * @param inputData 入参
     * @param request
     * @param response
     */
    @PostMapping(value = "/queryNodeInfoData.do")
    public void queryNodeInfoData(String inputData, HttpServletRequest request, HttpServletResponse response) {

        LOGGER.info("===== 查询指定节点数据 =====");
        LOGGER.info("inputData : {}", new Object[]{inputData});

        ResultVO resultVO = new ResultVO();
        resultVO.setIsSuccess(NumberEnum.ONE_STR.getNumberStr());

        ResGetDataForNodeVO dataForNodeVO = zkClientService.getDataForNodePath(inputData, null, null);
        resultVO.setResultData(dataForNodeVO);

        String resultJson = gson.toJson(resultVO);
        LOGGER.info("result info : {}", new Object[]{resultJson});

        flushResultToPage(response, resultJson);
    }

    /**
     * 查询子节点列表
     *
     * @param inputData 入参信息
     * @param request
     * @param response
     */
    @PostMapping(value = "/queryChildNodeList.do")
    public void queryChildNodeList(String inputData, HttpServletRequest request, HttpServletResponse response) {

        LOGGER.info("===== 查询子节点列表 =====");
        LOGGER.info("inputData : {}", new Object[]{inputData});

        ResultVO resultVO = new ResultVO();
        resultVO.setIsSuccess(NumberEnum.ONE_STR.getNumberStr());

        ResGetChildNodeVO resGetChildNodeVO = zkClientService.getChildNode(inputData, null);
        resultVO.setResultData(resGetChildNodeVO);

        String resultJson = gson.toJson(resultVO);
        LOGGER.info("result info : {}", new Object[]{resultJson});

        flushResultToPage(response, resultJson);

    }

    /**
     * 断开ZooKeeper服务端连接
     *
     * @param request
     * @param response
     */
    @PostMapping(value = "/closeZooKeeperServerConn.do")
    public void closeZooKeeperServerConn(HttpServletRequest request, HttpServletResponse response) {

        LOGGER.info("===== 关闭ZooKeeper 连接 =====");

        ResultVO resultVO = new ResultVO();
        resultVO.setIsSuccess(NumberEnum.ONE_STR.getNumberStr());

        ResCloseZKClientConnVO closeZKClientConnVO = zkClientService.closeZKClientConn();
        resultVO.setResultData(closeZKClientConnVO);

        String resultJson = gson.toJson(resultVO);
        LOGGER.info("result info : {}", new Object[]{resultJson});

        flushResultToPage(response, resultJson);
    }

    /**
     * 更新节点中的数据
     *
     * @param inputData
     * @param request
     * @param response
     */
    @PostMapping(value = "/setDataForNode.do")
    public void setDataForNode(String inputData, HttpServletRequest request, HttpServletResponse response) {

        LOGGER.info("===== 更新数据到节点中 =====");
        LOGGER.info("inputData : {}", new Object[]{inputData});

        ResultVO resultVO = new ResultVO();
        resultVO.setIsSuccess(NumberEnum.ONE_STR.getNumberStr());

        ResSetDataForNodeVO setDataForNodeVO = zkClientService.setDataForNodePath(inputData, -1);

        resultVO.setResultData(setDataForNodeVO);

        String resultJson = gson.toJson(resultVO);
        LOGGER.info("result info : {}", new Object[]{resultJson});

        flushResultToPage(response, resultJson);

    }

    /**
     * 添加子节点
     * <p>
     * 注意：添加节点不支持配置，默认创建永久节点。
     *
     * @param parentNode 父节点
     * @param childNode  要添加的子节点
     * @param nodeData   节点中数据
     * @param request
     * @param response
     */
    @PostMapping(value = "/addChildNode.do")
    public void addChildNode(String parentNode, String childNode, String nodeData, HttpServletRequest request, HttpServletResponse response) {

        LOGGER.info("===== 添加子节点 =====");
        LOGGER.info("parentNode : {} , childNode : {}", new Object[]{parentNode, childNode});

        ResultVO resultVO = new ResultVO();
        resultVO.setIsSuccess(NumberEnum.ONE_STR.getNumberStr());

        //process
        ResCreateOneNodeVO createOneNodeVO = zkClientService.createOneNode(parentNode, childNode, nodeData, ZooDefs.Ids.OPEN_ACL_UNSAFE, PERSISTENT);

        resultVO.setResultData(createOneNodeVO);

        String resultJson = gson.toJson(resultVO);
        LOGGER.info("result info : {}", new Object[]{resultJson});

        flushResultToPage(response, resultJson);
    }

    /**
     * 删除选中节点
     *
     * @param nodePath 节点路径
     * @param request
     * @param response
     */
    @PostMapping(value = "/deleteChildNode.do")
    public void deleteChildNode(String nodePath, HttpServletRequest request, HttpServletResponse response) {

        LOGGER.info("===== 删除节点 =====");
        LOGGER.info("nodePath : {}", new Object[]{nodePath});

        ResultVO resultVO = new ResultVO();
        resultVO.setIsSuccess(NumberEnum.ONE_STR.getNumberStr());

        //process
        ResDeleteNodeVO deleteNodeVO = zkClientService.deleteNode(nodePath, -1);

        resultVO.setResultData(deleteNodeVO);

        String resultJson = gson.toJson(resultVO);
        LOGGER.info("result info : {}", new Object[]{resultJson});

        flushResultToPage(response, resultJson);
    }

    /**
     * 删除节点-包含该节点下所有的子节点
     *
     * @param nodePath 节点路径
     * @param request
     * @param response
     */
    @PostMapping(value = "/deleteAllChildNodes.do")
    public void deleteAllChildNodes(String nodePath, HttpServletRequest request, HttpServletResponse response) {

        LOGGER.info("===== 删除节点以及所有子节点 =====");
        LOGGER.info("nodePath : " + nodePath);

        ResultVO resultVO = new ResultVO();
        resultVO.setIsSuccess(NumberEnum.ONE_STR.getNumberStr());

        // process
        ResDeleteNodeVO deleteNodeVO = zkClientService.deleteAllNodes(nodePath, -1);

        resultVO.setResultData(deleteNodeVO);

        String resultJson = gson.toJson(resultVO);
        LOGGER.info("result info : {}", new Object[]{resultJson});

        flushResultToPage(response, resultJson);
    }

    /**
     * 创建完整节点路径
     * <p>
     * 用户在页面直接输入完整节点路径，根据"/"进行分隔，每一层节点都判断节点是否存在，不存在则创建，存在则不管。
     * <p>
     * 用户输入信息存储在最后一个节点中。
     * <p>
     * 如果创建其中某个节点出现异常，之前创建的节点不会删除回滚
     *
     * @param nodePath 完整节点路径
     * @param nodeData 节点数据(存储在最后一个节点)
     * @param request
     * @param response
     */
    @PostMapping(value = "/creatCompleteNodes.do")
    public void createCompleteNodePath(String nodePath, String nodeData, HttpServletRequest request, HttpServletResponse response) {

        LOGGER.info("===== 删除节点以及所有子节点 =====");
        LOGGER.info("nodePath : " + nodePath);

        ResultVO resultVO = new ResultVO();
        resultVO.setIsSuccess(NumberEnum.ONE_STR.getNumberStr());

        //process
        ResCreateAllNodeVO resCreateAllNodeVO = zkClientService.createNodes(nodePath, nodeData, ZooDefs.Ids.OPEN_ACL_UNSAFE, PERSISTENT);

        resultVO.setResultData(resCreateAllNodeVO);

        String resultJson = gson.toJson(resultVO);
        LOGGER.info("result info : {}", new Object[]{resultJson});

        flushResultToPage(response, resultJson);
    }

    /**
     * 文件上传
     *
     * @param request
     * @param response
     */
    @PostMapping(value = "/fileUpLoad.do")
    public void fileUpLoad(HttpServletRequest request, HttpServletResponse response) {

        LOGGER.info("===== 文件上传 =====");

        ResultVO resultVO = new ResultVO();
        resultVO.setIsSuccess(NumberEnum.ONE_STR.getNumberStr());

        ResUploadFileVO resUploadFileVO = null;
        try {

            //从cookie中获取 父节点路径
            String uploadFilePath = PubUtils.getCookieStr(request, CommConstant.UPLOADFILEPATH_KEY);

            //文件名、文件内容
            MultipartFile multipartFile = PubUtils.getMultipartFile(request);
            String fileName = multipartFile.getOriginalFilename();
            String fileInfo = IOUtils.toString(multipartFile.getInputStream(), CommConstant.CODING_UTF8);

            LOGGER.info("uploadFilePath : {} , fileName ：{} , fileInfo : {}", new Object[]{uploadFilePath, fileName, fileInfo});

            //process
            resUploadFileVO = zkClientService.uploadFileWithCreateNode(uploadFilePath, fileName, fileInfo);

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        resultVO.setResultData(resUploadFileVO);
        String resultJson = gson.toJson(resultVO);
        flushResultToPage(response, resultJson);
    }


}
