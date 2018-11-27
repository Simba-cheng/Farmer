package com.server.vo.response;

import lombok.Data;

/**
 * 错误信息
 *
 * @author CYX
 * @create 2018-11-19-23:33
 */
@Data
public class ResErrorInfo {

    /**
     * 操作zk-错误码
     */
    private String errorCode;

    /**
     * 操作zk-具体错误信息
     */
    private String errorMessage;

    public ResErrorInfo() {
    }

    public ResErrorInfo(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
