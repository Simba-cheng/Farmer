package com.server.controller.zk;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.server.constant.NumberStrEnum;
import com.server.service.ZooKeeperClientService;
import com.server.util.PubUtils;
import com.server.vo.response.ResCloseZKClientConnVO;
import com.server.vo.response.ResCreateOneNodeVO;
import com.server.vo.response.ResDeleteNodeVO;
import com.server.vo.response.ResGetChildNodeVO;
import com.server.vo.response.ResGetDataForNodeVO;
import com.server.vo.response.ResSetDataForNodeVO;
import com.server.vo.response.ResZKClientConectVO;
import com.server.vo.response.ResultVO;
import org.apache.zookeeper.ZooDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static org.apache.zookeeper.CreateMode.PERSISTENT;

/**
 * zookeeper节点命名中不推荐使用'_'、'-',会造成数据解析冲突
 *
 * @author CYX
 * @date 2018/11/10 17:10
 */
@Controller
@RequestMapping(value = "/zk")
public class ZooKeeperController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZooKeeperController.class);

    @Autowired
    private ZooKeeperClientService zkClientService;

    @Autowired
    private PubUtils pubUtils;

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
        resultVO.setIsSuccess(NumberStrEnum.ONE_STR.getNumberStr());

        ResZKClientConectVO resZKClientConectVO = zkClientService.zkClientConect(host);
        resultVO.setResultData(resZKClientConectVO);

        String resultJson = gson.toJson(resultVO);

        LOGGER.info("result info : {}", new Object[]{resultJson});

        pubUtils.flushResultToPage(response, resultJson);
    }

    /**
     * 关闭zk client 链接
     *
     * @param request
     * @param response
     */
    @GetMapping(value = "/close.do")
    public void zkClientClose(HttpServletRequest request, HttpServletResponse response) {

        LOGGER.info("===== 关闭ZooKeeper服务端的连接 =====");

        ResultVO resultVO = new ResultVO();
        resultVO.setIsSuccess(NumberStrEnum.ONE_STR.getNumberStr());

        ResCloseZKClientConnVO resCloseZKClientConnVO = zkClientService.closeZKClientConn();
        resultVO.setResultData(resCloseZKClientConnVO);

        String resultJson = gson.toJson(resultVO);
        LOGGER.info("result info : {}", new Object[]{resultJson});

        pubUtils.flushResultToPage(response, resultJson);
    }

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

        resultVO.setIsSuccess(NumberStrEnum.ONE_STR.getNumberStr());
        ResGetChildNodeVO childNodeVO = zkClientService.getChildNode(inputData, null);

        if (null == childNodeVO.getErrorInfo()) {
            resultVO.setResultData(childNodeVO);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("resultVO", resultVO);

            String htmlData = pubUtils.getOutputHtmlStr(map, "nodeInfo.ftl");
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

        pubUtils.flushResultToPage(response, resultJSON);

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
        resultVO.setIsSuccess(NumberStrEnum.ONE_STR.getNumberStr());

        ResGetDataForNodeVO dataForNodeVO = zkClientService.getDataForNodePath(inputData, null, null);
        resultVO.setResultData(dataForNodeVO);

        String resultJson = gson.toJson(resultVO);
        LOGGER.info("result info : {}", new Object[]{resultJson});

        pubUtils.flushResultToPage(response, resultJson);
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
        resultVO.setIsSuccess(NumberStrEnum.ONE_STR.getNumberStr());

        ResGetChildNodeVO resGetChildNodeVO = zkClientService.getChildNode(inputData, null);
        resultVO.setResultData(resGetChildNodeVO);

        String resultJson = gson.toJson(resultVO);
        LOGGER.info("result info : {}", new Object[]{resultJson});

        pubUtils.flushResultToPage(response, resultJson);

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
        resultVO.setIsSuccess(NumberStrEnum.ONE_STR.getNumberStr());

        ResCloseZKClientConnVO closeZKClientConnVO = zkClientService.closeZKClientConn();
        resultVO.setResultData(closeZKClientConnVO);

        String resultJson = gson.toJson(resultVO);
        LOGGER.info("result info : {}", new Object[]{resultJson});

        pubUtils.flushResultToPage(response, resultJson);
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
        resultVO.setIsSuccess(NumberStrEnum.ONE_STR.getNumberStr());

        ResSetDataForNodeVO setDataForNodeVO = zkClientService.setDataForNodePath(inputData, -1);

        resultVO.setResultData(setDataForNodeVO);

        String resultJson = gson.toJson(resultVO);
        LOGGER.info("result info : {}", new Object[]{resultJson});

        pubUtils.flushResultToPage(response, resultJson);

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
        resultVO.setIsSuccess(NumberStrEnum.ONE_STR.getNumberStr());

        //process
        ResCreateOneNodeVO createOneNodeVO = zkClientService.createOneNode(parentNode, childNode, nodeData, ZooDefs.Ids.OPEN_ACL_UNSAFE, PERSISTENT);

        resultVO.setResultData(createOneNodeVO);

        String resultJson = gson.toJson(resultVO);
        LOGGER.info("result info : {}", new Object[]{resultJson});

        pubUtils.flushResultToPage(response, resultJson);
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
        resultVO.setIsSuccess(NumberStrEnum.ONE_STR.getNumberStr());

        //process
        ResDeleteNodeVO deleteNodeVO = zkClientService.deleteNode(nodePath, -1);

        resultVO.setResultData(deleteNodeVO);

        String resultJson = gson.toJson(resultVO);
        LOGGER.info("result info : {}", new Object[]{resultJson});

        pubUtils.flushResultToPage(response, resultJson);
    }

    ///**
    // * 删除节点-包含该节点下所有的子节点
    // *
    // * @param nodePath 节点路径
    // * @param request
    // * @param response
    // */
    //@PostMapping(value = "/deleteAllChildNodes.do")
    //public void deleteAllChildNodes(String nodePath, HttpServletRequest request, HttpServletResponse response) {
    //
    //    LOGGER.info("===== 删除节点以及所有子节点 =====");
    //    LOGGER.info("nodePath : " + nodePath);
    //
    //    ResultVO resultVO = new ResultVO();
    //    resultVO.setIsSuccess(NumberStrEnum.ONE_STR.getNumberStr());
    //
    //    // process
    //    ResDeleteNodeVO deleteNodeVO = zkClientService.deleteAllNodes(nodePath, -1);
    //
    //    resultVO.setResultData(deleteNodeVO);
    //
    //    String resultJson = gson.toJson(resultVO);
    //    LOGGER.info("result info : {}", new Object[]{resultJson});
    //
    //    pubUtils.flushResultToPage(response, resultJson);
    //}

}
