package com.server.vo.response;

import lombok.Data;

/**
 * 节点信息
 *
 * @author CYX
 * @create 2018-11-17-15:09
 */
@Data
public class ResNodeInfoVO {

    /**
     * 节点信息
     */
    private String nodePath;

    /**
     * 完整节点路径
     */
    private String completeNode;

    /**
     * 是否存在子节点
     * 1-存在，0-不存在
     */
    private String isExistenceChild;

    /**
     * 节点是否是文件形式
     * 1-是，0-不是
     * 用于展示的图标变更
     */
    private String nodeIsFile;

    public ResNodeInfoVO() {
    }

    public ResNodeInfoVO(String nodePath, String completeNode, String isExistenceChild, String nodeIsFile) {
        this.nodePath = nodePath;
        this.completeNode = completeNode;
        this.isExistenceChild = isExistenceChild;
        this.nodeIsFile = nodeIsFile;
    }
}
