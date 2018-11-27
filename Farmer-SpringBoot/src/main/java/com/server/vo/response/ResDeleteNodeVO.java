package com.server.vo.response;

import lombok.Data;

/**
 * @author CYX
 * @Date: 2018/11/15 15:28
 */
@Data
public class ResDeleteNodeVO {

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

    public ResDeleteNodeVO() {
    }

    public ResDeleteNodeVO(String isSuccess, ResErrorInfo errorInfo, String displayCopy, String nodePath) {
        this.isSuccess = isSuccess;
        this.errorInfo = errorInfo;
        this.displayCopy = displayCopy;
        this.nodePath = nodePath;
    }
}
