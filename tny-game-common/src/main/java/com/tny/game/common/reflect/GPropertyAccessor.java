package com.tny.game.common.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

public interface GPropertyAccessor {

    public String getName();

    public boolean isReadable();

    public boolean isWritable();

    public Class<?> getPropertyType();

    public Type getGenericType();

    public void setPropertyValue(Object instance, Object object) throws InvocationTargetException;

    public Object getPorpertyValue(Object instance) throws InvocationTargetException;

}
