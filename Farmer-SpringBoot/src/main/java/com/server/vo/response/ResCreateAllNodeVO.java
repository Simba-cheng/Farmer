package com.server.vo.response;

import lombok.Data;

/**
 * @author CYX
 * @date: 2018/12/15 10:15
 */
@Data
public class ResCreateAllNodeVO {

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

    public ResCreateAllNodeVO() {
    }

    public ResCreateAllNodeVO(String isSuccess, ResErrorInfo errorInfo, String displayCopy, String nodePath) {
        this.isSuccess = isSuccess;
        this.errorInfo = errorInfo;
        this.displayCopy = displayCopy;
        this.nodePath = nodePath;
    }
}
