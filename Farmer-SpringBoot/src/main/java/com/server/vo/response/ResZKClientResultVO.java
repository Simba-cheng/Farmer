package com.server.vo.response;


import java.util.List;

/**
 * @author CYX
 * @create 2018-11-14-11:52
 */

public class ResZKClientResultVO {

    /**
     * 操作zk-成功失败标识
     * <p>
     * 'Y'-成功
     * 'N'-失败
     */
    private String isSuccess;

    /**
     * 错误信息
     */
    private ResErrorInfo errorInfo;

    /**
     * 展示文案
     */
    private String displayCopy;

    /**
     * 节点信息
     */
    private String nodePath;

    /**
     * 子节点信息
     */
    private List<String> childNodePath;

    public String getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(String isSuccess) {
        this.isSuccess = isSuccess;
    }

    public ResErrorInfo getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(ResErrorInfo errorInfo) {
        this.errorInfo = errorInfo;
    }

    public String getDisplayCopy() {
        return displayCopy;
    }

    public void setDisplayCopy(String displayCopy) {
        this.displayCopy = displayCopy;
    }

    public String getNodePath() {
        return nodePath;
    }

    public void setNodePath(String nodePath) {
        this.nodePath = nodePath;
    }

    public List<String> getChildNodePath() {
        return childNodePath;
    }

    public void setChildNodePath(List<String> childNodePath) {
        this.childNodePath = childNodePath;
    }

    public ResZKClientResultVO() {
    }

    public ResZKClientResultVO(String isSuccess, ResErrorInfo errorInfo, String displayCopy, String nodePath, List<String> childNodePath) {
        this.isSuccess = isSuccess;
        this.errorInfo = errorInfo;
        this.displayCopy = displayCopy;
        this.nodePath = nodePath;
        this.childNodePath = childNodePath;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ResZKClientResultVO{");
        sb.append("isSuccess='").append(isSuccess).append('\'');
        sb.append(", errorInfo=").append(errorInfo);
        sb.append(", displayCopy='").append(displayCopy).append('\'');
        sb.append(", nodePath='").append(nodePath).append('\'');
        sb.append(", childNodePath=").append(childNodePath);
        sb.append('}');
        return sb.toString();
    }
}
