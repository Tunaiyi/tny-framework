package com.tny.game.net.message.common;

import com.tny.game.net.message.*;

/**
 * Created by Kun Yang on 2018/8/20.
 */
public class CommonMessageHeaderTest extends MessageHeaderTest {

    @Override
    public MessageHeader create(int id, int protocol, int code, long time, int toMessage, Object head) {
        return new CommonMessageHeader()
                .setId(id)
                .setCode(code)
                .setProtocol(protocol)
                .setTime(time)
                .setToMessage(toMessage)
                .setHead(head);
    }

}
