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
