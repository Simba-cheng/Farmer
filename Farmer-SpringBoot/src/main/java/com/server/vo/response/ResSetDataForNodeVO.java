package com.server.vo.response;


/**
 * @author CYX
 * @Date: 2018/11/15 14:56
 */

public class ResSetDataForNodeVO {

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
     * 节点信息
     */
    private String data;

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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public ResSetDataForNodeVO() {
    }

    public ResSetDataForNodeVO(String isSuccess, ResErrorInfo errorInfo, String displayCopy, String nodePath, String data) {
        this.isSuccess = isSuccess;
        this.errorInfo = errorInfo;
        this.displayCopy = displayCopy;
        this.nodePath = nodePath;
        this.data = data;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ResSetDataForNodeVO{");
        sb.append("isSuccess='").append(isSuccess).append('\'');
        sb.append(", errorInfo=").append(errorInfo);
        sb.append(", displayCopy='").append(displayCopy).append('\'');
        sb.append(", nodePath='").append(nodePath).append('\'');
        sb.append(", data='").append(data).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
