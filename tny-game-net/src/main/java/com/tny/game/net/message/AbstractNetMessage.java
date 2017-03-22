package com.tny.game.net.message;

import com.tny.game.LogUtils;
import com.tny.game.common.context.Attributes;
import com.tny.game.common.context.ContextAttributes;
import com.tny.game.common.reflect.Wraper;
import com.tny.game.net.session.Session;
import com.tny.game.protoex.ProtoExEnum;

public abstract class AbstractNetMessage<UID> implements NetMessage<UID> {

    protected volatile transient Attributes attributes;

    private MessageMode mode;
    private Session<UID> session;

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
                body.getClass(), clazz));
    }

    @SuppressWarnings("unchecked")
    private <T> T parseParam(Class<T> clazz, Object object) {
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

    protected abstract AbstractNetMessage<UID> setID(int ID);

    protected abstract AbstractNetMessage<UID> setProtocol(int protocol);

    protected abstract AbstractNetMessage<UID> setSign(String sign);

    protected abstract AbstractNetMessage<UID> setTime(long time);

    protected abstract AbstractNetMessage<UID> setBody(Object body);

    protected abstract AbstractNetMessage<UID> setCode(int code);

    protected abstract AbstractNetMessage<UID> setToMessage(int toMessage);

    protected abstract AbstractNetMessage<UID> setSession(Session<UID> session);

}
