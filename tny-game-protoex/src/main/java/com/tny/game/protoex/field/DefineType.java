/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
        this.ID = (byte) id;
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
