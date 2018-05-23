package com.tny.game.suite.base.converter;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import com.tny.game.base.item.xml.String2Enum;
import com.tny.game.common.formula.*;
import com.tny.game.common.formula.mvel.MvelFormulaFactory;
import org.slf4j.*;

/**
 * string转formula
 *
 * @author KGTny
 */
public class String2Template extends AbstractSingleValueConverter {

    private static Logger LOG = LoggerFactory.getLogger(String2Enum.class);

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
            return MvelFormulaFactory.create(formula, FormulaType.TEMPLATE);
        } catch (RuntimeException e) {
            LOG.error(formula, e);
            throw e;
        }
    }
}
