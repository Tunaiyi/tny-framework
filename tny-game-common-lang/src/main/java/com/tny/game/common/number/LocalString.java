package com.tny.game.common.number;

/**
 * 本地变量
 * Created by Kun Yang on 16/2/21.
 */
public class LocalString implements CharSequence {

    private String value;

    public String set(String value) {
        return this.value = value;
    }

    public String appand(Object value) {
        return this.value = value.toString();
    }

    @Override
    public int length() {
        return value.length();
    }

    @Override
    public char charAt(int index) {
        return value.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return value.subSequence(start, end);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
