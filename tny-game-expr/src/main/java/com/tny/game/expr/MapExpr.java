package com.tny.game.expr;

import java.util.*;

public abstract class MapExpr extends AbstractExpr {

    /**
     * 屬性
     * <p>
     * qualifier="key:java.lang.String java.lang.Object"
     */
    protected Map<String, Object> attribute;

    protected MapExpr(Number number) {
        super(number);
    }

    protected MapExpr(String expression) {
        super(expression);
    }

    protected MapExpr(MapExpr expr) {
        super(expr);
    }

    @Override
    protected Map<String, Object> attribute() {
        if (attribute == null) {
            attribute = new HashMap<>();
        }
        return attribute;
    }

}
