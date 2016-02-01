package com.tny.game.math;

import com.tny.game.common.formula.FormulaHolder;
import com.tny.game.common.formula.FormulaType;
import com.tny.game.common.formula.MvelFormulaFactory;
import com.tny.game.common.utils.collection.CollectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Weight<V> {

    public static final Logger LOGGER = LoggerFactory.getLogger(Weight.class);

    private V value;

    private FormulaHolder weight;

    public Weight() {
    }

    public Weight(V value, String weight) {
        this.value = value;
        this.weight = MvelFormulaFactory.create(weight, FormulaType.EXPRESSION);
    }

    public Weight(V value, FormulaHolder weight) {
        this.value = value;
        this.weight = weight;
    }

    public V getValue() {
        return this.value;
    }

    public WeightNum<V> getWeight(Object... params) {
        int weightNum = 0;
        try {
            weightNum = this.weight.createFormula()
                    .putAll(CollectionUtil.attributes2Map(params))
                    .execute(Integer.class);
        } catch (Throwable e) {
            LOGGER.error("", e);
            weightNum = 0;
        }
        return new WeightNum<V>(this.value, weightNum);
    }

}
