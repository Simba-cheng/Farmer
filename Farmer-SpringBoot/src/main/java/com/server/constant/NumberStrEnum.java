package com.server.constant;

/**
 * @author CYX
 * @Date: 2018/11/15 11:46
 */
public enum NumberStrEnum {

    ZERO_STR("0"),
    ONE_STR("1"),
    TWO_STR("2");

    private String numberStr;

    public String getNumberStr() {
        return numberStr;
    }

    NumberStrEnum(String numberStr) {
        this.numberStr = numberStr;
    }
}
