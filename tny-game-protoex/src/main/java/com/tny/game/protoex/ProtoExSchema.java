/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.protoex;

import com.tny.game.protoex.field.*;

/**
 * ProtoEx类型描述结构
 *
 * @param <T>
 * @author KGTny
 */
public interface ProtoExSchema<T> {

    /**
     * 结构所属的protoExID
     *
     * @return
     */
    int getProtoExId();

    /**
     * 结构名字
     *
     * @return
     */
    String getName();

    /**
     * 是否是原生类型
     *
     * @return
     */
    boolean isRaw();

    /**
     * 按options编码方式将value(包含tag)的protoEx序列化字节数组写入outputStream
     *
     * @param outputStream 目标流
     * @param value        值
     * @param options      编码方式
     */
    void writeMessage(ProtoExOutputStream outputStream, T value, FieldOptions<?> options);

    /**
     * 按options编码方式将value(不包含tag)的protoEx序列化字节数组写入outputStream
     *
     * @param outputStream 目标流
     * @param value        值
     * @param options      编码方式
     */
    void writeValue(ProtoExOutputStream outputStream, T value, FieldOptions<?> options);

    /**
     * 按options编码方式从inputStream读取Message(包括读取Tag),优先按Tag描述读取,若tag信息不全则按options读取
     *
     * @param inputStream 源流
     * @param options     默认编码方式
     * @return
     */
    T readMessage(ProtoExInputStream inputStream, FieldOptions<?> options);

    /**
     * 按options编码方式从inputStream读取Message(不包括读取Tag),优先按Tag描述读取,若tag信息不全则按options读取
     *
     * @param inputStream
     * @param tag
     * @param options
     * @return
     */
    T readValue(ProtoExInputStream inputStream, Tag tag, FieldOptions<?> options);

}
