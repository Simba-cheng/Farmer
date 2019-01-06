package com.server.vo.response;

import lombok.Data;

/**
 * 文件上传返回vo
 *
 * @author CYX
 * @create 2019-01-05-17:46
 */
@Data
public class ResUploadFileVO {

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

    public ResUploadFileVO() {
    }

    public ResUploadFileVO(String isSuccess, ResErrorInfo errorInfo, String displayCopy, String nodePath) {
        this.isSuccess = isSuccess;
        this.errorInfo = errorInfo;
        this.displayCopy = displayCopy;
        this.nodePath = nodePath;
    }
}
