package com.server.constant;

/**
 * @author CYX
 * @Date: 2018/11/15 11:46
 */
public enum NumberEnum {

    ZERO_STR("0"),
    ZERO_NUM(0),
    ONE_STR("1"),
    ONE_NUM(1),
    TWO_STR("2"),
    TWO_NUM(2);

    private String numberStr;
    private int number;

    public String getNumberStr() {
        return numberStr;
    }

    public int getNumber() {
        return number;
    }

    NumberEnum(String numberStr) {
        this.numberStr = numberStr;
    }

    NumberEnum(int number) {
        this.number = number;
    }
}
