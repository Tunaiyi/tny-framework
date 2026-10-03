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
package com.tny.game.namespace.sharding;

import com.tny.game.namespace.algorithm.*;

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

    public static final int ALL_NEGATIVE = 0xFF >> 5;

    public static boolean same(int v1, int v2, int v3) {
        int value = 0;
        value = (v1 >>> 31) | value;
        value = (v2 >>> 31) << 1 | value;
        value = (v3 >>> 31) << 2 | value;
        return value == 0 || value == ALL_NEGATIVE;
    }

    public static void main(String[] args) {
        System.out.println(same(-1, 2, 3));
        System.out.println(same(-1, -2, 3));
        System.out.println(same(1, -2, -3));
        System.out.println(same(1, 2, -3));
        System.out.println(same(-1, -2, -3));
        System.out.println(same(1, 2, 3));

    }

}
