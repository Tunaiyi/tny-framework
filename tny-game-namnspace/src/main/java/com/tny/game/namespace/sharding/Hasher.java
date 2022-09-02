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
package com.tny.game.namespace.sharding;

/**
 * Hash计算器
 * <p>
 *
 * @author kgtny
 * @date 2022/7/9 04:31
 **/
public interface Hasher<T> {

    default long hash(T value, int seed, long max) {
        var code = Math.abs(hash(value, seed));
        if (max > 0L) {
            return code % max;
        }
        return code;
    }

    long hash(T value, int seed);

}
