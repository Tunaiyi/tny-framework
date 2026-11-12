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
