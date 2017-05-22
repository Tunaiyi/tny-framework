package com.tny.game.common.formula;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.util.MethodStub;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Map;
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
public class Expression extends AbstractMvelFormula {

    /**
     * 表达式
     *
     * @uml.property name="expression"
     */
    private volatile Serializable expression;
    /**
     * @uml.property name="expressionStr"
     */
    protected String expressionStr;

    private Number number;

    protected Expression(String expression, ParserContext parserContext, boolean lazy) {
        if (StringUtils.isNumeric(expression)) {
            this.number = NumberUtils.createNumber(expression);
        } else {
            this.expressionStr = expression;
            if (parserContext == null) {
                this.parserContext = createParserContext();
            } else {
                this.parserContext = parserContext;
            }
            this.expression = lazy ? null : this.getExpression();
            // System.out.println(lazy + " " + (this.expression == null) + " " +
            // (this.number == null));
        }
    }

    protected Expression(String expression, Map<String, Object> context, boolean lazy) {
        expression = StringUtils.replace(expression, "\n", "");
        if (StringUtils.isNumeric(expression)) {
            this.number = NumberUtils.createNumber(expression);
        } else {
            this.expressionStr = expression;
            this.parserContext = createParserContext();
            if (context != null) {
                for (Entry<String, Object> entry : context.entrySet()) {
                    Object value = entry.getValue();
                    if (value instanceof Class)
                        this.parserContext.addImport(entry.getKey(), (Class<?>) value);
                    if (value instanceof Method)
                        this.parserContext.addImport(entry.getKey(), (Method) value);
                    if (value instanceof MethodStub)
                        this.parserContext.addImport(entry.getKey(), (MethodStub) value);
                }
            }
            this.expression = lazy ? null : this.getExpression();
        }
    }

    private Expression(final Expression expression) {
        if (expression.number != null) {
            this.number = expression.number;
        } else {
            this.expressionStr = expression.expressionStr;
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
                if (this.expression != null)
                    return this.expression;
                this.expression = MVEL.compileExpression(this.expressionStr, this.parserContext);
                if (!MvelFormulaFactory.EXPR_INFO)
                    this.expressionStr = null;
                this.parserContext = null;
            }
        }
        return this.expression;
    }

    @Override
    public Formula createFormula() {
        return new Expression(this);
    }

    @Override
    protected Object execute() {
        if (this.number != null)
            return this.number;
        try {
            return MVEL.executeExpression(this.getExpression(), this.attribute);
        } catch (Throwable e) {
            throw new RuntimeException("执行 [" + this.expressionStr + "] 异常 ", e);
        }
    }

    @Override
    public String toString() {
        return "Expression [expressionStr=" + (this.number != null ? this.number : this.expressionStr) + "]";
    }

}
