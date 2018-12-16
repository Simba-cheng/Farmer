package com.server.bottom;

import com.server.constant.CommConstant;
import com.server.constant.NumberEnum;
import org.apache.commons.io.IOUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * ZooKeeper CRUD 原子操作
 *
 * @author CYX
 * @date 2018/11/11 16:50
 */
@Service
public class ZooKeeperClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZooKeeperClient.class);

    /**
     * zookeeper client 主要对象
     */
    private static ZooKeeper zooKeeperClient;

    /**
     * 受网络环境的影响，有时候网络连接不稳定，连接的时候比较长，zookeeper对象被new出来之后，客户端与服务端之间，还没有真正的建立连接;
     * 这时候，你去调用zookeeper对象去操作，是会出现异常的
     */
    private CountDownLatch connectedSignal = new CountDownLatch(1);

    /**
     * 标志位：zookeeper服务端是否连接成功
     */
    private boolean zkServerIsConn = false;

    /**
     * 创建节点
     *
     * @param nodePath   节点路径
     * @param data       节点中的数据
     * @param acl        节点的ACL策略 Ids.OPEN_ACL_UNSAFE
     * @param createMode 节点类型 PERSISTENT 持久型、PERSISTENT_SEQUENTIAL 持久顺序、EPHEMERAL 临时节点、EPHEMERAL_SEQUENTIAL 临时顺序节点
     * @return the actual path of the created node
     * @throws Exception
     */
    public String createOneNode(String nodePath, byte[] data, List<ACL> acl, CreateMode createMode) throws Exception {

        LOGGER.info("#createOneNode-nodePath : {} , data : {}", new Object[]{nodePath, IOUtils.toString(data, CommConstant.CODING_UTF8)});

        return zooKeeperClient.create(nodePath, data, acl, createMode);
    }

    /**
     * 节点路径是否存在
     *
     * @param nodePath 节点路径
     * @return 节点不存在返回null
     * @throws Exception
     */
    public Stat exitNodePath(String nodePath) throws Exception {
        LOGGER.info("#exitNodePath-nodePath : {}", new Object[]{nodePath});
        return zooKeeperClient.exists(nodePath, null);
    }

    /**
     * 删除节点
     *
     * @param nodePath 节点路径
     * @param version  版本号，默认:-1
     * @throws Exception
     */
    public void deleteNode(String nodePath, int version) throws Exception {
        LOGGER.info("#deleteNode-nodePath : {} , version : {}", new Object[]{nodePath, version});
        zooKeeperClient.delete(nodePath, version);
    }

    /**
     * set参数到指定节点下
     *
     * @param nodePath 节点路径
     * @param data     节点数据(1M)
     * @param version  版本号，默认:-1
     * @throws Exception
     */
    public Stat setDataForNodePath(String nodePath, byte[] data, int version) throws Exception {
        LOGGER.info("#setDataForNodePath-nodePath : {} , data : {},version : {}", new Object[]{nodePath, IOUtils.toString(data, CommConstant.CODING_UTF8), version});
        return zooKeeperClient.setData(nodePath, data, version);
    }

    /**
     * 获取节点下的数据
     *
     * @param nodePath 节点路径
     * @param watcher  监听回调类
     * @param stat     节点统计信息
     * @return 节点中的数据
     * @throws Exception
     */
    public byte[] getDataForNodePath(String nodePath, Watcher watcher, Stat stat) throws Exception {
        return zooKeeperClient.getData(nodePath, watcher, stat);
    }

    /**
     * 查询某节点下的所有子节点
     *
     * @param nodePath 节点路径
     * @param watcher  监听回调类
     * @return 返回所有子节点
     * @throws Exception
     */
    public List<String> getChildNodePath(String nodePath, Watcher watcher) throws Exception {
        return zooKeeperClient.getChildren(nodePath, watcher);
    }

    /**
     * 连接ZooKeeper服务端
     *
     * @param host ip:port(127.0.0.1:2181)
     * @throws Exception
     */
    public void connect(String host) throws Exception {

        LOGGER.info("===== start 初始化 连接ZooKeeper服务端 =====");
        LOGGER.info("zookeeper host:{}", new Object[]{host});

        zooKeeperClient = null;

        zooKeeperClient = new ZooKeeper(host, Integer.MAX_VALUE, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                //客户端连接服务端完成后，回调process()，返回对应状态枚举值
                if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                    connectedSignal.countDown();
                    LOGGER.info("===== end 初始化 连接ZooKeeper服务端 完成=====");
                    zkServerIsConn = true;
                }

                LOGGER.info("===== 回调 ====");
                LOGGER.debug(watchedEvent.getState().toString());
                LOGGER.debug(watchedEvent.getType().toString());

            }
        });
        connectedSignal.await();
    }

    /**
     * 关闭连接
     */
    public void closeClientConn() throws Exception {
        if (null != zooKeeperClient) {
            long startTime = System.currentTimeMillis();

            zooKeeperClient.close();
            zooKeeperClient = null;
            zkServerIsConn = false;
            long endTime = System.currentTimeMillis();

            LOGGER.info("关闭连接花费时间：***** " + (endTime - startTime) + " *****");
        }
    }

    /**
     * 判断当前是否已经连接、超时
     *
     * @return 0-连接正常，1-未连接，2-连接超时或其他连接异常
     */
    public String isConn() {

        String result = NumberEnum.ZERO_STR.getNumberStr();

        //未连接
        if (zooKeeperClient == null) {
            return NumberEnum.ONE_STR.getNumberStr();
        }

        //如果基本查询异常，则认为超时或其他连接异常，提示重新连接
        try {
            zooKeeperClient.getChildren(CommConstant.SLASH, null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            result = NumberEnum.TWO_STR.getNumberStr();
        }

        return result;
    }

    public CountDownLatch getConnectedSignal() {
        return connectedSignal;
    }

    public ZooKeeper getZooKeeperClient() {
        return zooKeeperClient;
    }

    public boolean isZkServerIsConn() {
        return zkServerIsConn;
    }
}
