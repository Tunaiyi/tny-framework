package com.tny.game.base.item.xml;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import com.tny.game.common.formula.FormulaHolder;
import com.tny.game.common.formula.FormulaType;
import com.tny.game.common.formula.MvelFormulaFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * string转formula
 *
 * @author KGTny
 */
public class String2Formula extends AbstractSingleValueConverter {

    private static Logger LOG = LoggerFactory.getLogger(String2Formula.class);

    @Override
    @SuppressWarnings("rawtypes")
    public boolean canConvert(Class clazz) {
        return FormulaHolder.class.isAssignableFrom(clazz);
    }

    @Override
    public Object fromString(String formula) {
        if (formula == null || formula.equals(""))
            return null;
        try {
            return MvelFormulaFactory.create(formula, FormulaType.EXPRESSION);
        } catch (RuntimeException e) {
            LOG.error(formula, e);
            throw e;
        }
    }
}
