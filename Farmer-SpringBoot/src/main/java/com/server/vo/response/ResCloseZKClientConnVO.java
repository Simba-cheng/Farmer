package com.server.vo.response;

import lombok.Data;

/**
 * @author CYX
 * @Date: 2018/11/15 11:26
 */
@Data
public class ResCloseZKClientConnVO {

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


    public ResCloseZKClientConnVO() {
    }

    public ResCloseZKClientConnVO(String isSuccess, ResErrorInfo errorInfo, String displayCopy) {
        this.isSuccess = isSuccess;
        this.errorInfo = errorInfo;
        this.displayCopy = displayCopy;
    }
}
