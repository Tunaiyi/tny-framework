/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

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
