package com.tny.game.common.reflect;

import java.lang.reflect.*;

public interface PropertyAccessor {

    String getName();

    boolean isReadable();

    boolean isWritable();

    Class<?> getPropertyType();

    Type getGenericType();

    void setPropertyValue(Object instance, Object object) throws InvocationTargetException;

    Object getPropertyValue(Object instance) throws InvocationTargetException;

}
