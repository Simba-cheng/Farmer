package com.server.service;

import com.server.vo.response.*;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.util.List;

/**
 * @author CYX
 * @date 2018/11/10 17:21
 */
public interface ZooKeeperClientService {

    /**
     * zk连接初始化
     *
     * @param host ip:port(127.0.0.1:2181)
     * @return
     */
    ResZKClientConectVO zkClientConect(String host);

    /**
     * 关闭zookeeper连接
     *
     * @return
     */
    ResCloseZKClientConnVO closeZKClientConn();

    /**
     * 创建单节点
     *
     * @param parentNode 父节点路径
     * @param childNode  要创建的子节点名称
     * @param nodeData   节点中的数据
     * @param acl        节点的ACL策略 Ids.OPEN_ACL_UNSAFE
     * @param createMode 节点类型 PERSISTENT 持久型、PERSISTENT_SEQUENTIAL 持久顺序、EPHEMERAL 临时节点、EPHEMERAL_SEQUENTIAL 临时顺序节点
     * @return
     */
    ResCreateOneNodeVO createOneNode(String parentNode, String childNode, String nodeData, List<ACL> acl, CreateMode createMode);

    /**
     * 创建节点-完整路径<br>
     * 按照'/'拆分，循环判断节点是否存在，一直到最后一个节点；如果不存在就创建<br>
     * 注意：<br>
     * data数据写到最后一个节点中<br>
     * 完整路径中所有的节点配置(acl、createMode)，都采用同一个
     *
     * @param nodePath   节点路径
     * @param data       节点中的数据
     * @param acl        节点的ACL策略 Ids.OPEN_ACL_UNSAFE
     * @param createMode 节点类型 PERSISTENT 持久型、PERSISTENT_SEQUENTIAL 持久顺序、EPHEMERAL 临时节点、EPHEMERAL_SEQUENTIAL 临时顺序节点
     * @return
     */
    ResCreateOneNodeVO createNodes(String nodePath, String data, List<ACL> acl, CreateMode createMode);

    /**
     * 以文件的形式创建节点并写入数据
     *
     * @param fileName
     * @param nodePath
     * @param data
     * @param acl
     * @param createMode
     * @return
     */
    ResZKClientResultVO createFileToNode(String fileName, String nodePath, String data, List<ACL> acl, CreateMode createMode);

    /**
     * 判断节点路径是否存在
     *
     * @param nodePath 节点路径
     * @return
     */
    ResExitNodePathVO exitNodePath(String nodePath);

    /**
     * 删除节点路径
     *
     * @param nodePath 节点路径
     * @param version  版本号，默认:-1
     * @return
     */
    ResDeleteNodeVO deleteNode(String nodePath, int version);

    /**
     * set参数到指定节点下
     *
     * @param inputData 入参
     * @param version   版本号，默认:-1
     * @return
     */
    ResSetDataForNodeVO setDataForNodePath(String inputData, int version);

    /**
     * 获取指定节点下的数据
     *
     * @param inputData 接口数据
     * @param watcher   回调类
     * @param stat      节点统计信息
     * @return
     */
    ResGetDataForNodeVO getDataForNodePath(String inputData, Watcher watcher, Stat stat);

    /**
     * 从指定节点下的文件类型节点中获取数据
     *
     * @param fileName 文件名
     * @param nodePath 节点名称
     * @param watcher  回调类
     * @param stat     节点统计信息
     * @return
     */
    ResZKClientResultVO getDataForNodePathWithFile(String fileName, String nodePath, Watcher watcher, Stat stat);

    /**
     * 获取指定节点下所有子节点
     *
     * @param inputData 接口入参
     * @param watcher   回调类
     * @return
     */
    ResGetChildNodeVO getChildNode(String inputData, Watcher watcher);

    /**
     * 查询指定节点是否存在子节点
     *
     * @param nodePath 查询节点
     * @return true-存在，false-不存在
     */
    boolean isExistenceChildNode(String nodePath);

    /**
     * 判断当前程序是否已连接ZooKeeper服务器
     *
     * @return true-已连接，false-未连接
     */
    ResErrorInfo whetherToConnect();
}
