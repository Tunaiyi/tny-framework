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

package com.tny.game.common.math;

/**
 * Created by Kun Yang on 2017/6/26.
 */
public class RandomObject<V> implements Comparable<RandomObject<V>> {

    private final V object;

    private final int value;

    public RandomObject(V object, int value) {
        this.object = object;
        this.value = value;
    }

    public V getObject() {
        return this.object;
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public int compareTo(RandomObject<V> o) {
        // 降序（原减法*−1 在极值下溢出翻号）
        return Integer.compare(o.getValue(), this.value);
    }

    @Override
    public String toString() {
        return this.object + ":" + this.value;
    }

}