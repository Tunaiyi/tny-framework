package com.tny.game.common.reflect.javassist;

import com.tny.game.common.reflect.MethodAccessor;
import com.tny.game.common.reflect.PropertyAccessor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

public class JSsistPropertyAccessor implements PropertyAccessor {

    private String name;
    private MethodAccessor reader;
    private MethodAccessor writer;
    private Class<?> type;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isReadable() {
        return reader != null;
    }

    @Override
    public boolean isWritable() {
        return writer != null;
    }

    @Override
    public Class<?> getPropertyType() {
        return type;
    }

    @Override
    public Type getGenericType() {
        if (reader != null) {
            return reader.getJavaMethod().getGenericReturnType();
        } else if (writer != null) {
            return writer.getJavaMethod().getGenericParameterTypes()[0];
        }
        return null;
    }

    @Override
    public Object getPropertyValue(Object instance) throws InvocationTargetException {
        if (instance == null)
            return null;
        if (reader == null)
            throw new UnsupportedOperationException(instance.getClass() + "不支持 [" + name + "] 属性 Getter 方法");
        return reader.invoke(instance, type);
    }

    protected void setName(String name) {
        this.name = name;
    }

    protected void setReader(MethodAccessor reader) {
        this.reader = reader;
    }

    protected void setWriter(MethodAccessor writer) {
        this.writer = writer;
    }

    protected void setType(Class<?> type) {
        this.type = type;
    }

    @Override
    public void setPropertyValue(Object instance, Object value) throws InvocationTargetException {
        if (instance == null)
            return;
        if (writer == null)
            throw new UnsupportedOperationException(instance.getClass() + "不支持 [" + name + "] 属性 Setter 方法");
        writer.invoke(instance, value);
    }

}
