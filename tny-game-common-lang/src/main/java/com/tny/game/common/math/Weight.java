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

import org.slf4j.*;

@Deprecated
public class Weight<V> {

    public static final Logger LOGGER = LoggerFactory.getLogger(Weight.class);

    private V value;

    // private FormulaHolder weight;

    public Weight() {
    }

    public Weight(V value, String weight) {
        this.value = value;
        // this.weight = MvelFormulaFactory.create(weight, FormulaType.EXPRESSION);
    }

    // public Weight(V value, FormulaHolder weight) {
    //     this.value = value;
    //     this.weight = weight;
    // }

    public V getValue() {
        return this.value;
    }

    public WeightNum<V> getWeight(Object... params) {
        int weightNum = 0;
        try {
            // weightNum = this.weight.createFormula()
            //         .putAll(CollectionAide.attributes2Map(params))
            //         .execute(Integer.class);
        } catch (Throwable e) {
            LOGGER.error("", e);
            weightNum = 0;
        }
        return new WeightNum<V>(this.value, weightNum);
    }

}
