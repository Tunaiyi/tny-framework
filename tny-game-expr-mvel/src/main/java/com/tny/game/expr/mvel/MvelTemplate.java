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

package com.tny.game.expr.mvel;

import com.google.common.collect.ImmutableMap;
import com.tny.game.expr.*;
import org.mvel2.templates.*;

/**
 * @author KGTny
 * @ClassName: Expression
 * @Description: 表达式包装类
 * @date 2011-10-25 上午1:32:48
 * <p>
 * 表达式包装类
 * <p>
 * <br>
 */
class MvelTemplate extends AbstractMvelExpr {

    /**
     * 表达式
     *
     * @uml.property name="expression"
     * @uml.associationEnd multiplicity="(1 1)"
     */
    private volatile CompiledTemplate expression;

    protected MvelTemplate(String expression, MvelExprContext context, boolean lazy) {
        super(expression, context);
        if (this.number == null) {
            this.expressionStr = expression;
            if (context == null) {
                this.context = context;
            }
            this.expression = lazy ? null : this.getExpression();
            // System.out.println(lazy + " " + (this.expression == null) + " " +
            // (this.number == null));
        }
        this.expression = lazy ? null : this.getExpression();
    }

    protected MvelTemplate(final MvelTemplate template) {
        super(template);
        this.expression = template.getExpression();
    }

    private CompiledTemplate getExpression() {
        if (this.expression == null) {
            synchronized (this) {
                if (this.expression != null) {
                    return this.expression;
                }
                this.expression = TemplateCompiler.compileTemplate(this.expressionStr, this.context.getParserContext());
            }
        }
        return this.expression;
    }

    @Override
    public Expr createExpr() {
        return new MvelTemplate(this);
    }

    @Override
    protected Object execute() {
        try {
            return TemplateRuntime.execute(this.getExpression(), this.attribute == null ? ImmutableMap.of() : this.attribute);
        } catch (Exception e) {
            throw new RuntimeException("执行 [" + this.expressionStr + "] 异常 ", e);
        }
    }

}
