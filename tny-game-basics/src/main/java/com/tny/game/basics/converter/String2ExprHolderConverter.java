/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.converter;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import com.tny.game.expr.*;
import org.slf4j.*;

public abstract class String2ExprHolderConverter extends AbstractSingleValueConverter {

    protected static Logger LOG = LoggerFactory.getLogger(String2ExprHolderConverter.class);

    protected ExprHolderFactory exprHolderFactory;

    public String2ExprHolderConverter(ExprHolderFactory exprHolderFactory) {
        this.exprHolderFactory = exprHolderFactory;
    }

    // protected void init() {
    // ExprContext context = exprHolderFactory.getContext();
    // for (Entry<String, Object> entry : this.formulaContext.entrySet()) {
    //     Object value = entry.getValue();
    //     if (value instanceof Class)
    //         context.importClassAs(entry.getKey(), (Class<?>) value);
    //     if (value instanceof Method)
    //         context.getParserContext().addImport(entry.getKey(), (Method) value);
    //     if (value instanceof MethodStub)
    //         context.getParserContext().addImport(entry.getKey(), (MethodStub) value);
    // }
    // }

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
        } catch (Throwable e) {
            LOG.error("{}", formula, e);
            throw e;
        }
    }

}