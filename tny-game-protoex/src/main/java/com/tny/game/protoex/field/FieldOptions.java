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
 * protoEx类型编解码配置
 *
 * @param <T>
 * @author KGTny
 */
public interface FieldOptions<T> {

    /**
     * 字段配置相关名字
     *
     * @return
     */
    String getName();

    /**
     * 字段索引
     *
     * @return
     */
    int getIndex();

    /**
     * ProtoEx相对应类型
     *
     * @return
     */
    Class<T> getDefaultType();

    /**
     * 整形数字类型编码方式
     *
     * @return
     */
    FieldFormat getFormat();

    /**
     * 是否显式写入字段对应类型
     *
     * @return
     */
    boolean isExplicit();

    /**
     * 若为Repeat(Collection)
     *
     * @return
     */
    boolean isPacked();

}
