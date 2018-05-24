package com.tny.game.suite.base.converter;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import com.tny.game.expr.FormulaType;
import com.tny.game.expr.mvel.MvelFormulaFactory;
import org.springframework.stereotype.Component;

import java.util.*;

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
