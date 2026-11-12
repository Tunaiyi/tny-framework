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
