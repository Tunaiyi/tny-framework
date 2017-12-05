package com.tny.game.common.utils.converter;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import com.tny.game.common.formula.FormulaType;
import com.tny.game.common.formula.MvelFormulaFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class String2List extends AbstractSingleValueConverter {

    @Override
    @SuppressWarnings("rawtypes")
    public boolean canConvert(Class type) {
        return List.class.isAssignableFrom(type);
    }

    @Override
    public Object fromString(String str) {
        return MvelFormulaFactory.create(str, FormulaType.EXPRESSION).createFormula().execute(List.class);
    }

}
