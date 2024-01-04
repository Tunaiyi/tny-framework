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
