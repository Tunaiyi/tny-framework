package com.tny.game.net.transport;

import com.tny.game.net.message.*;

import java.util.Optional;

/**
 * <p>
 *
 * @author kgtny
 * @date 2023/12/17 00:18
 **/
public interface ConnectIdentity extends Contact {


    /**
     * 会话唯一标识
     * 与 ContactId 可能不相同
     *
     * @return 会话唯一标识
     */
    long getIdentify();

    /**
     * 会话唯一标识
     *
     * @return 会话唯一标识
     */
    Object getIdentifyToken();

    /**
     * 会话唯一标识
     *
     * @return 会话唯一标识
     */
    default <T> T getIdentifyToken(Class<T> type) {
        var token = getIdentifyToken();
        if (type.isInstance(token)) {
            return type.cast(token);
        }
        throw new ClassCastException(token + " can not cast " + type);
    }

    /**
     * 会话唯一标识
     *
     * @return 会话唯一标识
     */
    default Optional<Object> identifyToken() {
        return Optional.ofNullable(getIdentifyToken());
    }

    /**
     * 根据类型会话唯一标识
     *
     * @return 会话唯一标识
     */
    default <T> Optional<T> identifyToken(Class<T> type) {
        var token = getIdentifyToken();
        if (type.isInstance(token)) {
            return Optional.of(type.cast(token));
        }
        return Optional.empty();
    }

    /**
     * 是否有唯一标识
     *
     * @return 有返回true，否则为false
     */
    default boolean hasIdentifyToken() {
        return getIdentifyToken() != null;
    }

}
