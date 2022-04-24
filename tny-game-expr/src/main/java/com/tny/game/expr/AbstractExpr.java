package com.tny.game.expr;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Map;

public abstract class AbstractExpr implements Expr, ExprHolder {

    protected Number number;

    /**
     * @uml.property name="expressionStr"
     */
    protected String expressionStr;

    protected abstract Map<String, Object> attribute();

    public AbstractExpr(Number number) {
        this.number = number;
        this.expressionStr = StringUtils.EMPTY;
    }

    public AbstractExpr(String expression) {
        this.expressionStr = expression;
        if (StringUtils.isNumeric(expression)) {
            this.number = NumberUtils.createNumber(expression);
        }
    }

    protected AbstractExpr(AbstractExpr expr) {
        this.number = expr.number;
        this.expressionStr = expr.expressionStr;
    }

    @Override
    public Expr put(final String key, final Object value) {
        this.attribute().put(key, value);
        return this;
    }

    @Override
    public Expr putAll(final Map<String, Object> attribute) {
        if (attribute != null) {
            this.attribute().putAll(attribute);
        }
        return this;
    }

    protected abstract Object execute() throws Exception;

    @Override
    public Expr clear() {
        this.attribute().clear();
        return this;
    }

    @Override
    public Expr remove(final String key) {
        this.attribute().remove(key);
        return this;
    }

    /**
     * 执行表达式计算,返回结果 <br>
     *
     * @param clazz 返回类型
     * @return 返回结果
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T execute(final Class<T> clazz) {
        try {
            Object object;
            if (this.number != null) {
                object = this.number;
            } else {
                object = this.execute();
            }
            if (object == null) {
                return null;
            }
            if (clazz == null) {
                return (T)object;
            }
            if (object instanceof Number) {
                final Number number = (Number)object;
                if (Long.class == clazz || long.class == clazz) {
                    object = number.longValue();
                }
                if (Integer.class == clazz || int.class == clazz) {
                    object = number.intValue();
                }
                if (Double.class == clazz || double.class == clazz) {
                    object = number.doubleValue();
                }
                if (Float.class == clazz || float.class == clazz) {
                    object = number.floatValue();
                }
                if (Byte.class == clazz || byte.class == clazz) {
                    object = number.byteValue();
                }
                if (Boolean.class == clazz || boolean.class == clazz) {
                    object = number.intValue() > 0;
                }
            }
            if (Boolean.class == clazz || boolean.class == clazz) {
                object = Boolean.parseBoolean(object.toString());
            }
            if (String.class == clazz) {
                object = object.toString();
            }
            return (T)object;
        } catch (Exception e) {
            throw new ExprException("Formula : [\n" + this.getClass() + "\n] execute exception", e);
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " [expressionStr=" + (this.number != null ? this.number : this.expressionStr) + "]";
    }

}
