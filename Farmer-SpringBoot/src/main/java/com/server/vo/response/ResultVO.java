package com.server.vo.response;

import lombok.Data;

/**
 * @author CYX
 * @create 2018-11-14-11:54
 */
@Data
public class ResultVO {

    /**
     * 接口级别-成功失败标识
     * <p>
     * 0-失败
     * 1-成功
     */
    private String isSuccess;

    /**
     * 数据节点
     */
    private Object resultData;

    public ResultVO(String isSuccess, Object resultData) {
        this.isSuccess = isSuccess;
        this.resultData = resultData;
    }

    public ResultVO() {
    }

    @Override
    public String toString() {
        return "ResultVO{" +
                "isSuccess='" + isSuccess + '\'' +
                ", resultData=" + resultData +
                '}';
    }
}
