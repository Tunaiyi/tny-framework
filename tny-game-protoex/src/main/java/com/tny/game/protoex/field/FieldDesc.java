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
 * 字段描述接口
 *
 * @param <T>
 * @author KGTny
 */
public interface FieldDesc<T> extends FieldOptions<T> {

    /**
     * 将value设置到message中与当前描述对应的字段
     *
     * @param message
     * @param value
     */
    public void setValue(Object message, T value);

    /**
     * 读取message中与当前描述对应的字段的值
     *
     * @param message
     * @param value
     */
    public T getValue(Object message);

}
