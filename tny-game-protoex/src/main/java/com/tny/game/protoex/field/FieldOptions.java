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
