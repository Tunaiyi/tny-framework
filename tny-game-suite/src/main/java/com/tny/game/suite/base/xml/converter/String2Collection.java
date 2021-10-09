package com.tny.game.suite.base.xml.converter;

import com.tny.game.basics.converter.*;
import com.tny.game.expr.*;

import java.util.Collection;

@SuppressWarnings({"rawtypes"})
public class String2Collection extends String2ExprHolderConverter {

    private final Class<? extends Collection> clazz;

    public String2Collection(ExprHolderFactory exprHolderFactory, Class<? extends Collection> clazz) {
        super(exprHolderFactory);
        this.clazz = clazz;
    }

    @Override
    public boolean canConvert(Class type) {
        return this.clazz.isAssignableFrom(type);
    }

    @Override
    public Object fromString(String str) {
        return this.exprHolderFactory.create(str).createExpr().execute(this.clazz);
    }

}
