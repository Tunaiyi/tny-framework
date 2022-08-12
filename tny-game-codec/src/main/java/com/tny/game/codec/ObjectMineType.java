/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
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
public class ObjectMineType<T> {

    private final Type type;

    private final String mineType;

    public static <T> ObjectMineType<T> of(Class<T> type) {
        return new ObjectMineType<>(type, null);
    }

    public static <T> ObjectMineType<T> of(ReferenceType<T> type) {
        return new ObjectMineType<>(type.getType(), null);
    }

    public static <T> ObjectMineType<T> of(Class<T> type, String mineType) {
        return new ObjectMineType<>(type, mineType);
    }

    public static <T> ObjectMineType<T> of(ReferenceType<T> type, String mineType) {
        return new ObjectMineType<>(type.getType(), mineType);
    }

    private ObjectMineType(Type type, String mineType) {
        this.type = type;
        this.mineType = mineType;
    }

    /**
     * @return 获取类型
     */
    public Type getType() {
        return type;
    }

    public boolean hasMineType() {
        return StringUtils.isNotBlank(mineType);
    }

    /**
     * @return 获取序列化类型
     */
    public String getMineType() {
        return mineType;
    }

    /**
     * 以当前MineType创建一个 type 的 ObjectMineType
     *
     * @param type 类型
     * @return 发挥新的
     */
    public <U> ObjectMineType<U> with(Class<U> type) {
        return new ObjectMineType<>(type, mineType);
    }

    /**
     * 以当前MineType创建一个 type 的 ObjectMineType
     *
     * @param type 类型
     * @return 发挥新的
     */
    public <U> ObjectMineType<U> with(ReferenceType<U> type) {
        return new ObjectMineType<>(type.getType(), mineType);
    }

    @Override
    public String toString() {
        return "ObjectMineType{" + "type=" + type +
                ", mineType='" + mineType + '\'' +
                '}';
    }

}
