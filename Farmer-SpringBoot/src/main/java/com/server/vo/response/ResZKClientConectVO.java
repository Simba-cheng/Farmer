package com.server.vo.response;

import lombok.Data;

/**
 * zookeeper 链接VO
 *
 * @author CYX
 * @Date: 2018/11/15 11:16
 */
@Data
public class ResZKClientConectVO {

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

    public ResZKClientConectVO() {
    }

    public ResZKClientConectVO(String isSuccess, ResErrorInfo errorInfo, String displayCopy) {
        this.isSuccess = isSuccess;
        this.errorInfo = errorInfo;
        this.displayCopy = displayCopy;
    }
}
