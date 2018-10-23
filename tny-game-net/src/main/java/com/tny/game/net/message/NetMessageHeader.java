package com.tny.game.net.message;

/**
 * Created by Kun Yang on 2018/7/23.
 */
public interface NetMessageHeader extends MessageHeader {

    // /**
    //  * 设置消息主体
    //  *
    //  * @param subject 消息主体
    //  * @return 返回 this
    //  */
    // NetMessageHeader updateSubject(MessageSubject subject);

    NetMessageHeader setId(long id);

    // NetMessageHeader setCode(int code);
    //
    // NetMessageHeader setTime(long time);
    //
    // NetMessageHeader setToMessage(long toMessage);
    //
    // NetMessageHeader setProtocol(int protocol);
    //
    // NetMessageHeader setHead(Object head);

}
