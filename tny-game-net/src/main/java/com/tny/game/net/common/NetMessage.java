package com.tny.game.net.common;

import com.tny.game.LogUtils;
import com.tny.game.common.context.Attributes;
import com.tny.game.common.context.ContextAttributes;
import com.tny.game.common.reflect.Wraper;
import com.tny.game.net.message.Message;
import com.tny.game.net.message.MessageMode;
import com.tny.game.net.session.Session;
import com.tny.game.protoex.ProtoExEnum;

public abstract class NetMessage<UID> implements Message<UID> {

    private static final long serialVersionUID = 1L;

    protected transient Session<UID> session;

    protected volatile transient Attributes attributes;

    protected MessageMode mode;

    @Override
    public UID getUserID() {
        return this.session == null ? null : this.session.getUID();
    }

    @Override
    public String getUserGroup() {
        return this.session == null ? Session.DEFAULT_USER_GROUP : this.session.getGroup();
    }

    @Override
    public Session getSession() {
        return this.session;
    }

    @Override
    public String getHostName() {
        return this.session.getHostName();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBody(Class<T> clazz) {
        Object body = this.getBody();
        if (body == null)
            return null;
        final Class<?> checkClass = !clazz.isPrimitive() ? clazz : Wraper.getWraper(clazz);
        if (checkClass.isInstance(body)) {
            return (T) body;
        } else if (Enum.class.isAssignableFrom(checkClass) && body instanceof String) {
            return (T) Enum.valueOf((Class<? extends Enum>) checkClass, body.toString());
        } else if (checkClass == Long.class) {
            Number number = Double.parseDouble(body.toString());
            body = number.longValue();
            return (T) body;
        } else if (Number.class.isInstance(body)) {
            Number number = (Number) body;
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
        T value = this.parseParam(clazz, body);
        if (value != null)
            return value;
        throw new ClassCastException(LogUtils.format(
                "{} body {} is not class {} ", this.getProtocol(),
                body == null ? null : body.getClass(), clazz));
    }

    protected <T> T parseParam(Class<T> clazz, Object object) {
        if (object instanceof Number && ProtoExEnum.class.isAssignableFrom(clazz) && Enum.class.isAssignableFrom(clazz)) {
            for (T value : clazz.getEnumConstants()) {
                if (((ProtoExEnum) value).getID() == ((Number) object).intValue())
                    return value;
            }
        }
        return (T) object;
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
    public MessageMode getMode() {
        if (mode == null)
            mode = MessageMode.getMode(this);
        return mode;
    }

    protected abstract Object getBody();

    public void setSession(Session<UID> session) {
        this.session = session;
    }

}
