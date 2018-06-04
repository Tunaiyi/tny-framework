package com.tny.game.expr;

import java.util.Map;

public abstract class MapFormula extends AbstractFormula {

    /**
     * 屬性
     * <p>
     * qualifier="key:java.lang.String java.lang.Object"
     */
    protected Map<String, Object> attribute;

    protected MapFormula(Map<String, Object> attribute) {
        this.attribute = attribute;
    }

    protected Map<String, Object> attribute() {
        return attribute;
    }

}
