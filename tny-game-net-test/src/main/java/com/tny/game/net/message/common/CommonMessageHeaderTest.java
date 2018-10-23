package com.tny.game.net.message.common;

import com.tny.game.net.message.*;
import com.tny.game.net.message.MessageHeaderTest;

/**
 * Created by Kun Yang on 2018/8/20.
 */
public class CommonMessageHeaderTest extends MessageHeaderTest {

    @Override
    public MessageHeader create(long id, int protocol, int code, long time, long toMessage, Object head) {
        return new CommonMessageHeader()
                .setId(id)
                .setCode(code)
                .setProtocol(protocol)
                .setTime(time)
                .setToMessage(toMessage)
                .setAttachment(head);
    }

}
