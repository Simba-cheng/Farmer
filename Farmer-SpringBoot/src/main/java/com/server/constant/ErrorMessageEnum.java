package com.server.constant;

/**
 * 错误信息
 *
 * @author CYX
 * @create 2018-11-24-10:00
 */
public enum ErrorMessageEnum {

    ZK_Client_ERROR_00("ZK_Client_ERROR_00","参数为空"),
    ZK_Client_ERROR_01("ZK_Client_ERROR_01","ZooKeeperHost为空"),
    ZK_Client_ERROR_02("ZK_Client_ERROR_02","连接ZooKeeper服务端失败"),
    ZK_Client_ERROR_03("ZK_Client_ERROR_03","断开ZooKeeper服务端连接失败"),
    ZK_Client_ERROR_04("ZK_Client_ERROR_04","node节点数据为空"),
    ZK_Client_ERROR_05("ZK_Client_ERROR_05","创建节点失败"),
    ZK_Client_ERROR_06("ZK_Client_ERROR_06","判断节点是否存在-操作异常"),
    ZK_Client_ERROR_07("ZK_Client_ERROR_07","删除节点-操作异常"),
    ZK_Client_ERROR_08("ZK_Client_ERROR_08","创建完整路径-操作异常"),
    ZK_Client_ERROR_09("ZK_Client_ERROR_09","节点数据为空"),
    ZK_Client_ERROR_10("ZK_Client_ERROR_10","更新数据-操作异常"),
    ZK_Client_ERROR_11("ZK_Client_ERROR_11","获取节点数据-操作异常"),
    ZK_Client_ERROR_12("ZK_Client_ERROR_12","zkClient已经连接服务端"),
    ZK_Client_ERROR_13("ZK_Client_ERROR_13","查询子节点异常"),
    ZK_Client_ERROR_14("ZK_Client_ERROR_14","参数解析、校验异常"),
    ZK_Client_ERROR_15("ZK_Client_ERROR_15","客户端尚未连接"),
    ZK_Client_ERROR_16("ZK_Client_ERROR_16","服务端连接超时,请重新连接"),
    ZK_Client_ERROR_17("ZK_Client_ERROR_17","删除全部节点-异常-中断删除");

    private String errorCode;

    private String errorMessage;

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    ErrorMessageEnum(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
