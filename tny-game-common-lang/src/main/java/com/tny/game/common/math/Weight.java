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

import org.slf4j.*;

@Deprecated
public class Weight<V> {

    public static final Logger LOGGER = LoggerFactory.getLogger(Weight.class);

    private V value;

    /** 权重字面量（表达式引擎依赖属上层模块，P1 分层禁止下沉；本类按数值字面量解析） */
    private String weight;

    public Weight() {
    }

    public Weight(V value, String weight) {
        this.value = value;
        this.weight = weight;
    }

    // public Weight(V value, FormulaHolder weight) {
    //     this.value = value;
    //     this.weight = weight;
    // }

    public V getValue() {
        return this.value;
    }

    public WeightNum<V> getWeight(Object... params) {
        // 断链修复：原公式体注释后恒返回 0，下游分桶聚合被 Infinity 挤桶
        if (this.weight == null || this.weight.trim().isEmpty()) {
            return new WeightNum<>(this.value, 0);
        }
        try {
            return new WeightNum<>(this.value, Integer.parseInt(this.weight.trim()));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("权重必须为整数字面量（表达式支持属上层能力）: " + this.weight, e);
        }
    }

}
