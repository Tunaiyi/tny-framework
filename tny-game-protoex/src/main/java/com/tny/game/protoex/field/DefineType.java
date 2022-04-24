package com.tny.game.protoex.field;

/**
 * protoEx类型的定义方式
 *
 * @author KGTny
 */
public enum DefineType {

    /**
     * 原生
     */
    RAW(0),

    /**
     * 自定义
     */
    CUSTOM(1);

    public final byte ID;

    private DefineType(int id) {
        this.ID = (byte)id;
    }

    public static DefineType get(boolean raw) {
        if (raw) {
            return RAW;
        }
        return CUSTOM;
    }

    public static DefineType get(int typeID) {
        if (typeID == 0) {
            return RAW;
        }
        return CUSTOM;
    }

}
