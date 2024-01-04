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

/**
 * protoEx描述模式上下文接口
 *
 * @author KGTny
 */
public interface ProtoExSchemaContext {

    /**
     * 通过 Type 获取对应的Schema
     *
     * @param type
     * @return
     */
    public <T> ProtoExSchema<T> getSchema(Class<?> type);

    /**
     * 通过protoExID获取对应的Schema
     *
     * @param protoExID
     * @param raw
     * @return
     */
    public <T> ProtoExSchema<T> getSchema(int protoExID, boolean raw);

    /**
     * 通过protoExID获取对应的Schema
     * 若protoExID为0时返回 defaultClass对应的Schema
     *
     * @param protoExID
     * @param raw
     * @param defaultClass
     * @return
     */
    public <T> ProtoExSchema<T> getSchema(int protoExID, boolean raw, Class<?> defaultClass);

}
