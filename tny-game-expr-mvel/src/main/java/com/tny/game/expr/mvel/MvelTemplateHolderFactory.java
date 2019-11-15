package com.tny.game.expr.mvel;

import com.tny.game.expr.*;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Kun Yang on 2018/6/4.
 */
public class MvelTemplateHolderFactory extends MvelExprHolderFactory {

    public MvelTemplateHolderFactory() {
    }

    public MvelTemplateHolderFactory(boolean lazy) {
        super(lazy);
    }

    @Override
    protected String preproccess(String expression) {
        return StringUtils.replace(StringUtils.trim(expression), "\n", "");
    }

    @Override
    protected ExprHolder createExprHolder(String expr) throws ExprException {
        return new MvelTemplate(expr, context, lazy);
    }

}
