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
package com.tny.game.codec;

import com.tny.game.common.type.*;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;

/**
 * 类的媒体类型
 *
 * <p>
 *
 * @author kgtny
 * @date 2022/7/1 14:52
 **/
public class ObjectMimeType<T> {

    private final Type type;

    private final String mimeType;

    public static <T> ObjectMimeType<T> of(Class<T> type) {
        return new ObjectMimeType<>(type, null);
    }

    public static <T> ObjectMimeType<T> of(ReferenceType<T> type) {
        return new ObjectMimeType<>(type.getType(), null);
    }

    public static <T> ObjectMimeType<T> of(Class<T> type, String mimeType) {
        return new ObjectMimeType<>(type, mimeType);
    }

    public static <T> ObjectMimeType<T> of(ReferenceType<T> type, String mimeType) {
        return new ObjectMimeType<>(type.getType(), mimeType);
    }

    private ObjectMimeType(Type type, String mimeType) {
        this.type = type;
        this.mimeType = mimeType;
    }

    /**
     * @return 获取类型
     */
    public Type getType() {
        return type;
    }

    public boolean hasMineType() {
        return StringUtils.isNotBlank(mimeType);
    }

    /**
     * @return 获取序列化类型
     */
    public String getMineType() {
        return mimeType;
    }

    /**
     * 以当前MineType创建一个 type 的 ObjectMineType
     *
     * @param type 类型
     * @return 发挥新的
     */
    public <U> ObjectMimeType<U> with(Class<U> type) {
        return new ObjectMimeType<>(type, mimeType);
    }

    /**
     * 以当前MineType创建一个 type 的 ObjectMineType
     *
     * @param type 类型
     * @return 发挥新的
     */
    public <U> ObjectMimeType<U> with(ReferenceType<U> type) {
        return new ObjectMimeType<>(type.getType(), mimeType);
    }

    @Override
    public String toString() {
        return "ObjectMineType{" + "type=" + type +
               ", mimeType='" + mimeType + '\'' +
               '}';
    }

}
