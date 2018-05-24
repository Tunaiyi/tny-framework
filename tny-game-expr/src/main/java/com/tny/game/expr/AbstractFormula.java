package com.tny.game.expr;

import java.util.*;

public abstract class AbstractFormula implements Formula, FormulaHolder {

    /**
     * 屬性
     * <p>
     * qualifier="key:java.lang.String java.lang.Object"
     */
    protected Map<String, Object> attribute = new HashMap<String, Object>();

    @Override
    public Formula put(final String key, final Object value) {
        this.attribute.put(key, value);
        return this;
    }

    @Override
    public Formula putAll(final Map<String, Object> attribute) {
        if (attribute != null)
            this.attribute.putAll(attribute);
        return this;
    }

    protected abstract Object execute();

    @Override
    public Formula clear() {
        this.attribute.clear();
        return this;
    }

    @Override
    public Formula remove(final String key) {
        this.attribute.remove(key);
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
        Object object = this.execute();
        if (object == null)
            return null;
        if (clazz == null)
            return (T) object;
        if (object instanceof Number) {
            final Number number = (Number) object;
            if (Long.class == clazz || long.class == clazz)
                object = number.longValue();
            if (Integer.class == clazz || int.class == clazz)
                object = number.intValue();
            if (Double.class == clazz || double.class == clazz)
                object = number.doubleValue();
            if (Float.class == clazz || float.class == clazz)
                object = number.floatValue();
            if (Byte.class == clazz || byte.class == clazz)
                object = number.byteValue();
        }
        if (Boolean.class == clazz || boolean.class == clazz)
            object = Boolean.parseBoolean(object.toString());
        if (String.class == clazz)
            object = object.toString();
        return (T) object;
    }

}
