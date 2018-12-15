package com.server.service.impl;

import com.server.bottom.ZooKeeperClient;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

import static org.mockito.Matchers.anyObject;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author CYX
 * @create 2018-11-14-12:30
 */
public class ZooKeeperClientServiceImplTest {

    @InjectMocks
    private ZooKeeperClientServiceImpl zooKeeperClientService;

    @Mock
    private ZooKeeperClient zooKeeperClient;

    @Mock
    private HttpServletRequest requestWithLogon;

    @Mock
    private HttpServletResponse response;

    @Mock
    private PrintWriter writer;

    //public static final String TEST_HOST = "192.168.137.150:2181";
    public static final String TEST_HOST = "127.0.0.1:2181";

    @BeforeClass(alwaysRun = true)
    public void init() {
        try {

            MockitoAnnotations.initMocks(this);

            //when(response.getWriter()).thenReturn(writer);

            //zooKeeperClientService.zkClientConect(TEST_HOST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_connectionZKServer() {
        zooKeeperClientService.zkClientConect(TEST_HOST);
    }

    @Test
    public void test_connectionZKServer_exception_1() {

        zooKeeperClientService.zkClientConect("");
    }

    @Test
    public void test_createNodes() {

        try {

            when(zooKeeperClient.exitNodePath((String) anyObject())).thenReturn(null);

            when(zooKeeperClient.createOneNode((String) anyObject(), anyObject(), anyObject(), anyObject())).thenReturn("yes");

            String nodePath = "/home/service/whois";
            String data = "whois";

            zooKeeperClientService.createNodes(nodePath, data, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
