package com.tny.game.base.converter;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import com.tny.game.common.formula.AbstractMvelFormula;
import com.tny.game.common.formula.FormulaHolder;
import com.tny.game.common.formula.FormulaType;
import com.tny.game.common.formula.MvelFormulaFactory;
import org.mvel2.ParserContext;
import org.mvel2.util.MethodStub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public abstract class MvelFormulaConverter extends AbstractSingleValueConverter {

    protected static Logger LOG = LoggerFactory.getLogger(MvelFormulaConverter.class);

    protected Map<String, Object> formulaContext = new HashMap<String, Object>();

    protected ParserContext parserContext = AbstractMvelFormula.createParserContext();

    private FormulaType type;

    public MvelFormulaConverter(FormulaType type) {
        this.type = type;
    }

    protected void init() {
        if (this.formulaContext != null) {
            for (Entry<String, Object> entry : this.formulaContext.entrySet()) {
                Object value = entry.getValue();
                if (value instanceof Class)
                    this.parserContext.addImport(entry.getKey(), (Class<?>) value);
                if (value instanceof Method)
                    this.parserContext.addImport(entry.getKey(), (Method) value);
                if (value instanceof MethodStub)
                    this.parserContext.addImport(entry.getKey(), (MethodStub) value);
            }
        }
    }

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
            return MvelFormulaFactory.create(formula, this.type, this.parserContext, false);
        } catch (Throwable e) {
            LOG.error("{}", formula, e);
            throw e;
        }
    }
}