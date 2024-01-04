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
        return (this.value - o.getValue()) * -1;
    }

    @Override
    public String toString() {
        return this.object + ":" + this.value;
    }

}