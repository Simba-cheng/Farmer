package com.server.init;

import com.server.bottom.ZooKeeperClient;
import com.server.constant.CommConstant;
import com.server.controller.zk.ZooKeeperController;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * SpringBoot 启动之后，自动调用该类
 * <p>
 * 1.初始化链接ZooKeeper服务端,格式:zkClientHost=127.0.0.1:2181,127.0.0.2:2181,127.0.0.3:2181
 *
 * @author CYX
 * @create 2018-11-13-23:19
 */
@Component
public class AutoStart implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoStart.class);

    @Autowired
    private ZooKeeperClient zooKeeperClient;

    @Override
    public void run(String... args) {

        LOGGER.info("===== SpringBoot启动完成，开始初始化工作 =====");
        LOGGER.info("args:{}", new Object[]{args});

        String zkHost = "";

        try {

            for (String str : args) {
                if (StringUtils.isEmpty(str)) {
                    continue;
                }

                LOGGER.info("str : {}", new Object[]{str});

                if (str.contains(CommConstant.CLI_KEY_ZK)) {
                    zkHost = str.substring(str.indexOf("=") + 1, str.length());
                }
            }

            if (StringUtils.isNotEmpty(zkHost)) {
                //zk client 初始化
                initZKClient(zkHost);
            } else {
                LOGGER.error("ZooKeeper Host 为空，尚未连接服务端...");
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * 初始化zk连接
     * <p>
     * 初始化zookeeper client失败，不影响程序的启动
     *
     * @param host
     */
    private void initZKClient(String host) {
        try {
            LOGGER.info("初始化zk客户端连接，host:{}", new Object[]{host});

            //存储zkHost变量
            ZooKeeperController.ZK_HOST = host;

            zooKeeperClient.connect(host);
        } catch (Exception e) {
            LOGGER.error("初始化zk客户端连接失败，host:{}", new Object[]{host});
            LOGGER.error(e.getMessage(), e);
        }
    }

}
