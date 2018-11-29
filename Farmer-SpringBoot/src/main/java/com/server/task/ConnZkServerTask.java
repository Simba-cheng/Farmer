package com.server.task;

import com.server.bottom.ZooKeeperClient;

/**
 * 连接ZooKeeper服务端的任务线程
 * <p>
 * 解决问题：
 * <p>
 * 用户通过页面按钮连接ZooKeeper服务器时，如果输错了host信息，ZooKeeper Client 会一直尝试连接。
 * 此时页面会一直卡死，用户体验太差。
 * <p>
 * <p>
 * 方案：
 * <p>
 * 通过线程方式，连接服务端，暂停五秒
 *
 * @author CYX
 * @create 2018-11-29-21:57
 */
public class ConnZkServerTask implements Runnable {

    private ZooKeeperClient zooKeeperClient;
    private String zkServerHost;

    public ConnZkServerTask(ZooKeeperClient zooKeeperClient, String zkServerHost) {
        this.zooKeeperClient = zooKeeperClient;
        this.zkServerHost = zkServerHost;
    }

    public ConnZkServerTask() {
    }

    @Override
    public void run() {

        try {
            zooKeeperClient.connect(zkServerHost);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
