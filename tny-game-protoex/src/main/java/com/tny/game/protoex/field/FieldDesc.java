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
