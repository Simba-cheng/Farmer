package com.server.service.impl;

import com.server.service.ZooKeeperClientService;
import org.junit.Test;

/**
 * @author CYX
 * @create 2018-11-14-12:30
 */
public class ZooKeeperClientServiceImplTest {

    private ZooKeeperClientService zooKeeperClient = new ZooKeeperClientServiceImpl();

    public static final String TEST_HOST = "192.168.137.150:2181";

    /**
     * 测试-创建节点-完整路径
     */
    @Test
    public void test_createNodes() {

        zooKeeperClient.createNodes("/one/two", "1234567", null, null);

    }

}
