package com.tny.game.expr;

/**
 * Created by Kun Yang on 2018/6/4.
 */
public interface ExprHolderFactory {

    ExprContext getContext();

    ExprHolder create(String formula) throws ExprException;

}
