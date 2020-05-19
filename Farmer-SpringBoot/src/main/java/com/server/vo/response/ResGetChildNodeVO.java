package com.server.vo.response;


import java.util.List;

/**
 * @author CYX
 * @Date: 2018/11/16 17:47
 */

public class ResGetChildNodeVO {

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
    private List<ResNodeInfoVO> childNodeInfoList;

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

    public List<ResNodeInfoVO> getChildNodeInfoList() {
        return childNodeInfoList;
    }

    public void setChildNodeInfoList(List<ResNodeInfoVO> childNodeInfoList) {
        this.childNodeInfoList = childNodeInfoList;
    }

    public ResGetChildNodeVO() {
    }

    public ResGetChildNodeVO(String isSuccess, ResErrorInfo errorInfo, String displayCopy, String nodePath, List<ResNodeInfoVO> childNodeInfoList) {
        this.isSuccess = isSuccess;
        this.errorInfo = errorInfo;
        this.displayCopy = displayCopy;
        this.nodePath = nodePath;
        this.childNodeInfoList = childNodeInfoList;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ResGetChildNodeVO{");
        sb.append("isSuccess='").append(isSuccess).append('\'');
        sb.append(", errorInfo=").append(errorInfo);
        sb.append(", displayCopy='").append(displayCopy).append('\'');
        sb.append(", nodePath='").append(nodePath).append('\'');
        sb.append(", childNodeInfoList=").append(childNodeInfoList);
        sb.append('}');
        return sb.toString();
    }
}
