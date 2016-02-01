package com.tny.game.suite.base.converter;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import com.tny.game.common.formula.FormulaType;
import com.tny.game.common.formula.MvelFormulaFactory;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class String2Set extends AbstractSingleValueConverter {

    @Override
    @SuppressWarnings("rawtypes")
    public boolean canConvert(Class type) {
        return Set.class.isAssignableFrom(type);
    }

    @Override
    public Object fromString(String str) {
        List<?> value = MvelFormulaFactory.create(str, FormulaType.EXPRESSION).createFormula().execute(List.class);
        if (value != null)
            return new HashSet<>(value);
        return new HashSet<>();
    }

}
