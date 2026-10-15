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
