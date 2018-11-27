package com.server.vo.response;

import lombok.Data;

import java.util.List;

/**
 * @author CYX
 * @create 2018-11-14-11:52
 */
@Data
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

    public ResZKClientResultVO() {
    }

    public ResZKClientResultVO(String isSuccess, ResErrorInfo errorInfo, String displayCopy, String nodePath, List<String> childNodePath) {
        this.isSuccess = isSuccess;
        this.errorInfo = errorInfo;
        this.displayCopy = displayCopy;
        this.nodePath = nodePath;
        this.childNodePath = childNodePath;
    }
}
