package com.server.vo.request;

import lombok.Data;

/**
 * 查询的节点信息
 *
 * @author CYX
 * @create 2018-11-17-21:11
 */
@Data
public class ReqNodeInfo {

    /**
     * zk服务器host
     */
    private String zkConnHost;

    /**
     * 需要查询的节点路径
     */
    private String nodePath;

    /**
     * 节点中的数据
     */
    private String nodeData;

    public ReqNodeInfo() {
    }

    public ReqNodeInfo(String zkConnHost, String nodePath, String nodeData) {
        this.zkConnHost = zkConnHost;
        this.nodePath = nodePath;
        this.nodeData = nodeData;
    }
}
