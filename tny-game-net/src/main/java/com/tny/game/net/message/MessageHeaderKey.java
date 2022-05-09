package com.tny.game.net.message;

import java.util.Objects;

/**
 * 消息头部信息键值
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/29 22:07
 **/

public class MessageHeaderKey<T extends MessageHeader<?>> {

    public final String key;

    private final Class<T> headerClass;

    public static <T extends MessageHeader<T>> MessageHeaderKey<T> key(String key, Class<T> headerClass) {
        return new MessageHeaderKey<>(key, headerClass);
    }

    private MessageHeaderKey(String key, Class<T> headerClass) {
        this.key = key;
        this.headerClass = headerClass;
    }

    public String getKey() {
        return key;
    }

    public Class<T> getHeaderClass() {
        return headerClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MessageHeaderKey)) {
            return false;
        }
        MessageHeaderKey<?> that = (MessageHeaderKey<?>)o;
        return getKey().equals(that.getKey()) && getHeaderClass().equals(that.getHeaderClass());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getHeaderClass());
    }

}
