/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.namespace;

/**
 * 基础 Hash 计算器
 * <p>
 *
 * @author kgtny
 * @date 2022/7/9 04:31
 **/
public abstract class BaseHasher<T> implements Hasher<T> {

    protected abstract long toHash(T value, int seed);

    @Override
    public long hash(T value, int seed) {
        return Math.abs(toHash(value, seed));
    }

}
