package com.tny.game.net.transport.message;

/**
 * Created by Kun Yang on 2018/7/23.
 */
public abstract class AbstractNetMessageHeader extends AbstractMessageHeader {

    protected abstract AbstractNetMessageHeader setId(long id);

    protected abstract AbstractNetMessageHeader setCode(int code);

    protected abstract AbstractNetMessageHeader setTime(long time);

    protected abstract AbstractNetMessageHeader setToMessage(long toMessage);

    protected abstract AbstractNetMessageHeader setProtocol(int protocol);

    protected abstract AbstractNetMessageHeader setHead(Object head);
}
