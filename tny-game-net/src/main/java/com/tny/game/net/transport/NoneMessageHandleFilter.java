package com.tny.game.net.transport;

import com.tny.game.net.message.Message;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * 无过滤的终端消息过滤器
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-11 17:22
 */
public class NoneMessageHandleFilter<UID> implements MessageHandleFilter<UID> {

    private static NoneMessageHandleFilter<?> FILTER = new NoneMessageHandleFilter<>();

    public static <I> MessageHandleFilter<I> getInstance() {
        return as(FILTER);
    }

    private NoneMessageHandleFilter() {
    }

    @Override
    public final boolean isCanHandler(Message<UID> message) {
        return true;
    }

}
