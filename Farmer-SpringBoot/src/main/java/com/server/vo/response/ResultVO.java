package com.server.vo.response;

/**
 * @author CYX
 * @create 2018-11-14-11:54
 */

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

    public String getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(String isSuccess) {
        this.isSuccess = isSuccess;
    }

    public Object getResultData() {
        return resultData;
    }

    public void setResultData(Object resultData) {
        this.resultData = resultData;
    }

    public ResultVO(String isSuccess, Object resultData) {
        this.isSuccess = isSuccess;
        this.resultData = resultData;
    }

    public ResultVO() {
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ResultVO{");
        sb.append("isSuccess='").append(isSuccess).append('\'');
        sb.append(", resultData=").append(resultData);
        sb.append('}');
        return sb.toString();
    }
}
