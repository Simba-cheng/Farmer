package com.server.vo.response;


/**
 * 节点信息
 *
 * @author CYX
 * @create 2018-11-17-15:09
 */

public class ResNodeInfoVO {

    /**
     * 节点信息
     */
    private String nodePath;

    /**
     * 完整节点路径
     */
    private String completeNode;

    /**
     * 是否存在子节点
     * 1-存在，0-不存在
     */
    private String isExistenceChild;

    /**
     * 节点是否是文件形式
     * 1-是，0-不是
     * 用于展示的图标变更
     */
    private String nodeIsFile;

    public String getNodePath() {
        return nodePath;
    }

    public void setNodePath(String nodePath) {
        this.nodePath = nodePath;
    }

    public String getCompleteNode() {
        return completeNode;
    }

    public void setCompleteNode(String completeNode) {
        this.completeNode = completeNode;
    }

    public String getIsExistenceChild() {
        return isExistenceChild;
    }

    public void setIsExistenceChild(String isExistenceChild) {
        this.isExistenceChild = isExistenceChild;
    }

    public String getNodeIsFile() {
        return nodeIsFile;
    }

    public void setNodeIsFile(String nodeIsFile) {
        this.nodeIsFile = nodeIsFile;
    }

    public ResNodeInfoVO() {
    }

    public ResNodeInfoVO(String nodePath, String completeNode, String isExistenceChild, String nodeIsFile) {
        this.nodePath = nodePath;
        this.completeNode = completeNode;
        this.isExistenceChild = isExistenceChild;
        this.nodeIsFile = nodeIsFile;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ResNodeInfoVO{");
        sb.append("nodePath='").append(nodePath).append('\'');
        sb.append(", completeNode='").append(completeNode).append('\'');
        sb.append(", isExistenceChild='").append(isExistenceChild).append('\'');
        sb.append(", nodeIsFile='").append(nodeIsFile).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
