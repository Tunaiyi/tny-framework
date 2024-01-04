/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.expr.mvel;

import com.google.common.collect.ImmutableMap;
import com.tny.game.expr.*;
import org.mvel2.*;
import org.mvel2.util.MethodStub;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;

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
class MvelExpression extends AbstractMvelExpr {

    /**
     * 表达式
     *
     * @uml.property name="expression"
     */
    private volatile Serializable expression;

    protected MvelExpression(String expression, MvelExprContext context, boolean lazy) {
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
    }

    protected MvelExpression(String expression, Set<Class<?>> importClasses, Map<String, Object> context, boolean lazy) {
        super(expression, new MvelExprContext());
        if (this.number != null) {
            this.expressionStr = expression;
            ParserContext parserContext = this.context.getParserContext();
            for (Class<?> cl : importClasses)
                parserContext.addImport(cl);
            if (context != null) {
                for (Entry<String, Object> entry : context.entrySet()) {
                    Object value = entry.getValue();
                    if (value instanceof Class) {
                        parserContext.addImport(entry.getKey(), (Class<?>) value);
                    }
                    if (value instanceof Method) {
                        parserContext.addImport(entry.getKey(), (Method) value);
                    }
                    if (value instanceof MethodStub) {
                        parserContext.addImport(entry.getKey(), (MethodStub) value);
                    }
                }
            }
            this.expression = lazy ? null : this.getExpression();
        }
    }

    private MvelExpression(final MvelExpression expression) {
        super(expression);
        if (expression.number == null) {
            this.expression = expression.getExpression();
        }
    }

    //	public static ParserContext getContext() {
    //		ParserContext PARSER_CONTEXT = threadLocal.get();
    //		if (PARSER_CONTEXT == null) {
    //			PARSER_CONTEXT = new ParserContext();
    //			for (final Method method : methodSet)
    //				PARSER_CONTEXT.addImport(method.getName(), method);
    //			PARSER_CONTEXT.addImport(ArrayList.class);
    //			PARSER_CONTEXT.addImport(HashSet.class);
    //			PARSER_CONTEXT.addImport(HashMap.class);
    //			threadLocal.set(PARSER_CONTEXT);
    //		}
    //		return PARSER_CONTEXT;
    //	}

    private Serializable getExpression() {
        if (this.expression == null) {
            synchronized (this) {
                if (this.expression != null) {
                    return this.expression;
                }
                this.expression = MVEL.compileExpression(this.expressionStr, this.context.getParserContext());
                if (!MvelExprHolderFactory.EXPR_INFO) {
                    this.expressionStr = null;
                }
            }
        }
        return this.expression;
    }

    @Override
    public Expr createExpr() {
        return new MvelExpression(this);
    }

    @Override
    protected Object execute() {
        try {
            return MVEL.executeExpression(this.getExpression(), this.attribute == null ? ImmutableMap.of() : this.attribute);
        } catch (Throwable e) {
            throw new RuntimeException("执行 [" + this.expressionStr + "] 异常 ", e);
        }
    }

    @Override
    public String toString() {
        return "Expression [expressionStr=" + (this.number != null ? this.number : this.expressionStr) + "]";
    }

}
