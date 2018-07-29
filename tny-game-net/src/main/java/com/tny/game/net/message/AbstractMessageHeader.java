package com.tny.game.net.message;

import com.tny.game.common.enums.EnumID;
import com.tny.game.common.reflect.Wraper;
import com.tny.game.common.utils.Logs;

import java.lang.reflect.*;

/**
 * Created by Kun Yang on 2018/7/23.
 */
public abstract class AbstractMessageHeader implements MessageHeader {

    protected AbstractMessageHeader() {
    }

    protected abstract Object getHead();

    @Override
    public boolean isHasHead() {
        return this.getHead() != null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getHead(ReferenceType<T> bodyClass) {
        return getHead(getClassType(bodyClass));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getHead(Class<T> clazz) {
        return conver2Type(clazz, this.getHead());
    }

    @SuppressWarnings("unchecked")
    protected <T> Class<T> getClassType(ReferenceType<T> referenceType) {
        Type[] types = referenceType.getClass().getGenericInterfaces();
        Type subType = ((ParameterizedType) types[0]).getActualTypeArguments()[0];
        Class<T> clazz = null;
        if (subType instanceof Class) {
            clazz = (Class<T>) subType;
        } else if (subType instanceof ParameterizedType) {
            clazz = (Class<T>) ((ParameterizedType) subType).getRawType();
        }
        return clazz;
    }

    @SuppressWarnings("unchecked")
    protected <T> T conver2Type(Class<T> clazz, Object object) {
        if (object == null)
            return null;
        final Class<?> checkClass = !clazz.isPrimitive() ? clazz : Wraper.getWraper(clazz);
        if (checkClass.isInstance(object)) {
            return (T) object;
        } else if (Enum.class.isAssignableFrom(checkClass) && object instanceof String) {
            return (T) Enum.valueOf((Class<? extends Enum>) checkClass, object.toString());
        } else if (checkClass == Long.class) {
            Number number = Double.parseDouble(object.toString());
            object = number.longValue();
            return (T) object;
        } else if (Number.class.isInstance(object)) {
            Number number = (Number) object;
            if (checkClass == Long.class) {
                return (T) ((Object) number.longValue());
            } else if (checkClass == Integer.class) {
                return (T) ((Object) number.intValue());
            } else if (checkClass == Byte.class) {
                return (T) ((Object) number.byteValue());
            } else if (checkClass == Float.class) {
                return (T) ((Object) number.floatValue());
            } else if (checkClass == Short.class) {
                return (T) ((Object) number.shortValue());
            }
        }
        T value = this.parseParam(clazz, object);
        if (value != null)
            return value;
        throw new ClassCastException(Logs.format(
                "{} body {} is not class {} ", this.getProtocol(),
                object.getClass(), clazz));

    }

    @SuppressWarnings("unchecked")
    private <T> T parseParam(Class<T> clazz, Object object) {
        if (object instanceof Number && EnumID.class.isAssignableFrom(clazz)
                && Enum.class.isAssignableFrom(clazz)) {
            for (T value : clazz.getEnumConstants()) {
                Object id = ((EnumID) value).getID();
                if (id instanceof Number) {
                    if (((Number) id).intValue() == ((Number) object).intValue())
                        return value;
                } else {
                    throw new IllegalArgumentException(Logs.format(
                            "can not find Enum {} where id is {}", clazz, object));
                }
            }
        }
        return (T) object;
    }

}
