package com.server.dto;


/**
 * @author CYX
 * @Date: 2018/11/20 10:59
 */

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

    public String getZkConnHost() {
        return zkConnHost;
    }

    public void setZkConnHost(String zkConnHost) {
        this.zkConnHost = zkConnHost;
    }

    public String getNodePath() {
        return nodePath;
    }

    public void setNodePath(String nodePath) {
        this.nodePath = nodePath;
    }

    public String getNodeData() {
        return nodeData;
    }

    public void setNodeData(String nodeData) {
        this.nodeData = nodeData;
    }

    public NodeInfoDTO() {
    }

    public NodeInfoDTO(String zkConnHost, String nodePath, String nodeData) {
        this.zkConnHost = zkConnHost;
        this.nodePath = nodePath;
        this.nodeData = nodeData;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("NodeInfoDTO{");
        sb.append("zkConnHost='").append(zkConnHost).append('\'');
        sb.append(", nodePath='").append(nodePath).append('\'');
        sb.append(", nodeData='").append(nodeData).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
