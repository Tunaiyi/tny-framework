package com.tny.game.net.message;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * 消息头部信息
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/29 15:56
 **/
public abstract class MessageHeader<T extends MessageHeader<T>> {

    public abstract String getKey();

    public T getValue() {
        return as(this);
    }

}
