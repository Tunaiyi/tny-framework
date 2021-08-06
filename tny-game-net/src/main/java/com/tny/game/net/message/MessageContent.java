package com.tny.game.net.message;

import com.tny.game.common.type.*;

import java.util.Optional;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * Created by Kun Yang on 2017/2/16.
 */
public interface MessageContent extends Protocol {

    /**
     * @return 获取结果码
     */
    int getCode();

    /**
     * @return 获取消息模式
     */
    MessageMode getMode();

    /**
     * @return 响应消息
     */
    long getToMessage();

    /**
     * @return 是否存在消息
     */
    boolean existBody();

    /**
     * @return 获取消息体
     */
    Object getBody();

    /**
     * @return 获取消息体
     */
    <T> T bodyAs(Class<T> clazz);

    /**
     * @return 获取消息体
     */
    <T> T bodyAs(ReferenceType<T> clazz);

    /**
     * @return 获取消息体
     */
    default <T> Optional<T> bodyIf(Class<T> clazz) {
        Object body = getBody();
        if (clazz.isInstance(body)) {
            return Optional.of(as(body));
        }
        return Optional.empty();
    }

}
