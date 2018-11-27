package com.server.vo.response;

import lombok.Data;

import java.util.List;

/**
 * @author CYX
 * @Date: 2018/11/16 17:47
 */
@Data
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

    public ResGetChildNodeVO() {
    }

    public ResGetChildNodeVO(String isSuccess, ResErrorInfo errorInfo, String displayCopy, String nodePath, List<ResNodeInfoVO> childNodeInfoList) {
        this.isSuccess = isSuccess;
        this.errorInfo = errorInfo;
        this.displayCopy = displayCopy;
        this.nodePath = nodePath;
        this.childNodeInfoList = childNodeInfoList;
    }
}
