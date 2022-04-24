package com.tny.game.basics.item.xml;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import com.tny.game.expr.*;
import org.slf4j.*;

/**
 * string转formula
 *
 * @author KGTny
 */
public class String2Formula extends AbstractSingleValueConverter {

    private static Logger LOG = LoggerFactory.getLogger(String2Formula.class);

    private ExprHolderFactory exprHolderFactory;

    public String2Formula(ExprHolderFactory exprHolderFactory) {
        this.exprHolderFactory = exprHolderFactory;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean canConvert(Class clazz) {
        return ExprHolder.class.isAssignableFrom(clazz);
    }

    @Override
    public Object fromString(String formula) {
        if (formula == null || formula.equals("")) {
            return null;
        }
        try {
            return exprHolderFactory.create(formula);
        } catch (RuntimeException e) {
            LOG.error(formula, e);
            throw e;
        }
    }

}
