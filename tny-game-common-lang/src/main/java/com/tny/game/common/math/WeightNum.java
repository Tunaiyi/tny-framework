/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.common.math;

@Deprecated
public class WeightNum<V> {

    private V value;

    private Integer weight;

    protected WeightNum(V value, Integer weight) {
        this.value = value;
        this.weight = weight;
    }

    public V getValue() {
        return this.value;
    }

    public int getWeight() {
        return this.weight;
    }

    public int countProbability(float perNum) {
        return (int)(this.weight * perNum);
    }

}
