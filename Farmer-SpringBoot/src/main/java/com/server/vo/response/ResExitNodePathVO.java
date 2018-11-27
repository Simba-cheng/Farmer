package com.server.vo.response;

import lombok.Data;

/**
 * @author CYX
 * @Date: 2018/11/15 11:37
 */
@Data
public class ResExitNodePathVO {
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
     * 节点是否存在
     * 0-不存在
     * 1-存在
     */
    private String isExist;

    public ResExitNodePathVO() {
    }

    public ResExitNodePathVO(String isSuccess, ResErrorInfo errorInfo, String displayCopy, String nodePath, String isExist) {
        this.isSuccess = isSuccess;
        this.errorInfo = errorInfo;
        this.displayCopy = displayCopy;
        this.nodePath = nodePath;
        this.isExist = isExist;
    }
}
