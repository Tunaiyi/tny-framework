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
