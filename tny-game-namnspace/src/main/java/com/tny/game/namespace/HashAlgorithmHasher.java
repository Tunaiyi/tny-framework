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

import com.tny.game.namespace.consistenthash.*;

import java.util.function.Function;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.namespace.NamespaceConstants.*;

/**
 * HashAlgorithm 算法 Hash 计算器
 * 可以引用一个 Hash 算法对象实现 Hash 计算器
 * <p>
 *
 * @author kgtny
 * @date 2022/7/9 04:31
 **/
public class HashAlgorithmHasher<T> implements Hasher<T> {

    private final HashAlgorithm algorithm;

    private final long maxSlots;

    private final Function<T, String> toKey;

    public static <T> HashAlgorithmHasher<T> hasher() {
        return HashAlgorithmHasher.hasher(Object::toString);
    }

    public static <T> HashAlgorithmHasher<T> hasher(long maxSlots) {
        return HashAlgorithmHasher.hasher(Object::toString, maxSlots);
    }

    public static <T> HashAlgorithmHasher<T> hasher(Function<T, String> toKey) {
        return hasher(toKey, null);
    }

    public static <T> HashAlgorithmHasher<T> hasher(Function<T, String> toKey, long maxSlots) {
        return hasher(toKey, null, maxSlots);
    }

    public static <T> HashAlgorithmHasher<T> hasher(Function<T, String> toKey, HashAlgorithm algorithm) {
        return hasher(toKey, algorithm, UNLIMITED_SLOT_SIZE);
    }

    public static <T> HashAlgorithmHasher<T> hasher(Function<T, String> toKey, HashAlgorithm algorithm, long maxSlots) {
        return new HashAlgorithmHasher<>(toKey, algorithm, maxSlots);
    }

    private HashAlgorithmHasher(Function<T, String> toKey, HashAlgorithm algorithm, long maxSlots) {
        this.algorithm = ifNull(algorithm, HashAlgorithms.getDefault());
        this.toKey = toKey;
        this.maxSlots = maxSlots;
    }

    @Override
    public long hash(T value, int seed) {
        long hashCode = algorithm.hash(toKey.apply(value), seed);
        if (maxSlots < 0) {
            return hashCode;
        }
        return hashCode % maxSlots;
    }

    public long getMax() {
        return maxSlots > 0 ? maxSlots : algorithm.getMax();
    }

}
