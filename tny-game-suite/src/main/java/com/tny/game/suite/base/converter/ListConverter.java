package com.tny.game.suite.base.converter;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import com.tny.game.common.formula.Formula;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

public class ListConverter extends AbstractSingleValueConverter {

    @Autowired
    protected GameFormulaConverter formulaConverter;

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public boolean canConvert(Class clazz) {
        return clazz.isAssignableFrom(ArrayList.class);
    }

    @Override
    public Object fromString(String value) {
        return ((Formula) formulaConverter.fromString(value)).execute(Object.class);
    }

}
