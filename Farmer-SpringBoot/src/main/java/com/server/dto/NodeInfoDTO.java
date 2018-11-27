package com.server.dto;

import lombok.Data;

/**
 * @author CYX
 * @Date: 2018/11/20 10:59
 */
@Data
public class NodeInfoDTO {

    /**
     * zk服务器host
     */
    private String zkConnHost;

    /**
     * 节点信息
     */
    private String nodePath;

    /**
     * 节点中的数据
     */
    private String nodeData;

    public NodeInfoDTO() {
    }

    public NodeInfoDTO(String zkConnHost, String nodePath, String nodeData) {
        this.zkConnHost = zkConnHost;
        this.nodePath = nodePath;
        this.nodeData = nodeData;
    }
}
