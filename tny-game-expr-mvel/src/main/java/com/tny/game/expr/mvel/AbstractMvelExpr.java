package com.tny.game.expr.mvel;

import com.tny.game.expr.*;

public abstract class AbstractMvelExpr extends MapExpr {

    protected MvelExprContext context;

    protected AbstractMvelExpr(String expression, MvelExprContext context) {
        super(expression);
        this.context = context;
    }

    protected AbstractMvelExpr(AbstractMvelExpr expr) {
        super(expr);
        this.context = expr.context;
    }

}
