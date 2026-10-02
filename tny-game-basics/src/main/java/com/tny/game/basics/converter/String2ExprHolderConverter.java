/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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