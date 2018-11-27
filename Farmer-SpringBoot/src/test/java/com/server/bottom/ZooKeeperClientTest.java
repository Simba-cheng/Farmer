package com.server.bottom;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

import java.io.File;
import java.util.List;

/**
 * @author CYX
 * @Date: 2018/11/12 10:36
 */
public class ZooKeeperClientTest {

    private ZooKeeperClient zooKeeperClient = new ZooKeeperClient();

    public static final String TEST_HOST = "192.168.137.150:2181";

    @Test
    public void test_connect() {
        try {
            zooKeeperClient.connect(TEST_HOST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建节点
     */
    @Test
    public void test_createOneNode() {
        try {
            zooKeeperClient.connect(TEST_HOST);
            String nodePath = zooKeeperClient.createOneNode("/test", "123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            System.out.println("创建节点:" + nodePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件上传(将文件挂载到指定节点下)
     * 注意：文件挂载前，父节点要存在
     */
    @Test
    public void test_fileUpLoadToNode() {
        try {
            byte[] fileResultArr = FileUtils.readFileToByteArray(new File("./temp/test.txt"));
            zooKeeperClient.connect(TEST_HOST);
            String nodePath = zooKeeperClient.createOneNode("/test/test.txt", fileResultArr, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            System.out.println("上传文件：" + nodePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 节点是否存在
     */
    @Test
    public void test_exitNodePath() {
        try {
            //String nodePath = "/test";
            String nodePath = "/test123";
            zooKeeperClient.connect(TEST_HOST);
            Stat stat = zooKeeperClient.exitNodePath(nodePath);
            if (null == stat) {
                System.out.println(nodePath + "节点不存在");
            } else {
                System.out.println(nodePath + "节点存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除节点
     */
    @Test
    public void test_deleteNodePath() {
        String nodePath = "/test0000000005";
        try {
            zooKeeperClient.connect(TEST_HOST);
            zooKeeperClient.deleteNode(nodePath, -1);
            System.out.println("删除" + nodePath + "成功");
        } catch (Exception e) {
            System.out.println("删除" + nodePath + "节点失败");
            e.printStackTrace();
        }
    }

    /**
     * 插入数据到指定节点
     */
    @Test
    public void test_setDataForNodePath() {
        String nodePath = "/test";
        try {
            zooKeeperClient.connect(TEST_HOST);
            Stat stat = zooKeeperClient.setDataForNodePath(nodePath, "99999****".getBytes(), -1);
            if (null == stat) {
                System.out.println("插入内容失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取指定节点下的数据
     */
    @Test
    public void test_getDataForNodePath() {
        try {
            zooKeeperClient.connect(TEST_HOST);
            byte[] resultArr = zooKeeperClient.getDataForNodePath("/test", null, null);
            System.out.println(IOUtils.toString(resultArr, "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取指定节点下的子节点
     */
    @Test
    public void test_getChildNodePath() {
        try {
            zooKeeperClient.connect(TEST_HOST);
            List<String> nodes = zooKeeperClient.getChildNodePath("/", null);
            System.out.println(nodes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
