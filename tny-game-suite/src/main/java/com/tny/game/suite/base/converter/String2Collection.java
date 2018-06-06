package com.tny.game.suite.base.converter;

import com.tny.game.base.converter.String2ExprHolderConverter;
import com.tny.game.expr.ExprHolderFactory;

import java.util.Collection;

public class String2Collection extends String2ExprHolderConverter {

    private Class<? extends Collection> clazz;

    public String2Collection(ExprHolderFactory exprHolderFactory, Class<? extends Collection> clazz) {
        super(exprHolderFactory);
        this.clazz = clazz;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean canConvert(Class type) {
        return clazz.isAssignableFrom(type);
    }

    @Override
    public Object fromString(String str) {
        return exprHolderFactory.create(str).createExpr().execute(clazz);
    }

}
