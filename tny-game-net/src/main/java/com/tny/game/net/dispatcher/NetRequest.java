package com.tny.game.net.dispatcher;

import com.tny.game.common.context.Attributes;
import com.tny.game.common.context.ContextAttributes;
import com.tny.game.net.base.MessageType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class NetRequest extends Request {

    private static final long serialVersionUID = 1L;

    protected static final Map<Class<?>, Class<?>> WRAPPER_MAP;

    static {
        Map<Class<?>, Class<?>> wrapperMap = new HashMap<Class<?>, Class<?>>();
        wrapperMap.put(int.class, Integer.class);
        wrapperMap.put(boolean.class, Boolean.class);
        wrapperMap.put(byte.class, Byte.class);
        wrapperMap.put(char.class, Character.class);
        wrapperMap.put(short.class, Short.class);
        wrapperMap.put(long.class, Long.class);
        wrapperMap.put(float.class, Float.class);
        wrapperMap.put(double.class, Double.class);
        WRAPPER_MAP = Collections.unmodifiableMap(wrapperMap);
    }

    protected transient Session session;

    protected volatile transient Attributes attributes;

    @Override
    public boolean isTimeOut(long duration) {
        return duration > 0L && Math.abs(System.currentTimeMillis() - this.getTime()) > duration;
    }

    @Override
    public long getUserID() {
        return this.session == null ? Session.UN_LOGIN_UID : this.session.getUID();
    }

    @Override
    public String getUserGroup() {
        return this.session == null ? Session.DEFAULT_USER_GROUP : this.session.getGroup();
    }

    @Override
    public boolean isLogin() {
        return this.session == null ? false : this.session.isAskerLogin();
    }

    @Override
    public Session getSession() {
        return this.session;
    }

    @Override
    protected void requestBy(Session session) {
        this.session = session;
    }

    @Override
    public String getHostName() {
        return this.session.getHostName();
    }

    protected abstract Object getParam(int index);

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> T getParameter(final int index, final Class<T> clazz) {
        if (index >= this.size())
            return null;
        Object object = this.getParam(index);
        if (object == null)
            return null;
        final Class<?> checkClass = !clazz.isPrimitive() ? clazz : this.getWrapper(clazz);
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
        T value = this.parseParam(index, clazz, object);
        if (value != null)
            return value;
        throw new ClassCastException(this.getProtocol() + " Number " + index + " param is not class of " + clazz);
    }

    protected <T> T parseParam(final int index, final Class<T> clazz, Object object) {
        return null;
    }

    @Override
    public MessageType getMessage() {
        return MessageType.REQUEST;
    }

    public Class<?> getWrapper(final Class<?> clazz) {
        Class<?> wapper = NetRequest.WRAPPER_MAP.get(clazz);
        if (wapper == null)
            return clazz;
        return wapper;
    }

    @Override
    public Object[] getParameters(final Class<?>[] clazzs) {
        int clazzsSize = clazzs == null ? 0 : clazzs.length;
        if (this.size() < clazzsSize)
            throw new IllegalArgumentException("clazzs size is : " + clazzsSize + " is not equals with paramList's size " + this.size());
        final Object[] objects = new Object[clazzsSize];
        for (int oIndex = 0; oIndex < clazzsSize; oIndex++)
            objects[oIndex] = this.getParameter(oIndex, clazzs[oIndex]);
        return objects;
    }

    @Override
    public Attributes attributes() {
        if (this.attributes != null)
            return this.attributes;
        synchronized (this) {
            if (this.attributes != null)
                return this.attributes;
            return this.attributes = ContextAttributes.create();
        }
    }

    @Override
    public boolean isHasAttributes() {
        return this.attributes != null && !this.attributes.isEmpty();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(938008755, 1404174567).appendSuper(super.hashCode()).append(this.getUserID()).append(this.getProtocol()).append(this.getUserGroup())
                .toHashCode();
    }

    /**
     * @see java.lang.Object#equals(Object)
     */
    @Override
    public boolean equals(final Object object) {
        if (!(object instanceof NetRequest)) {
            return false;
        }
        if (this == object)
            return true;
        NetRequest rhs = (NetRequest) object;
        return new EqualsBuilder().append(this.getUserID(), rhs.getUserID()).append(this.getProtocol(), rhs.getProtocol())
                .append(this.getUserGroup(), rhs.getUserGroup())
                .isEquals();
    }

    protected abstract void setCheckKey(String checkKey);

    protected abstract void setID(int id);

    protected abstract void setParamList(List<?> paramList);

    protected abstract void setProtocol(int protocol);

    protected abstract void setTime(long time);

    protected abstract void addParam(int index, Object parameter);

    protected abstract void addParam(Object parameter);

    protected abstract void addAllParam(List<Object> parameterList);

    @Override
    public String toString() {
        return "AbstractRequset [checkKey=" + this.getCheckKey() + ", hostName=" + this.getHostName() + ", protocol=" + this.getProtocol() + ", time="
                + this.getTime() + ", paramList="
                + this.getParamList()
                + "]";
    }

}
