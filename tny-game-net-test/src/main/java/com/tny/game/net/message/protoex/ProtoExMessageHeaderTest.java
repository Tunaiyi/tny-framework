package com.tny.game.net.message.protoex;

import com.tny.game.net.message.*;
import com.tny.game.net.message.MessageHeaderTest;

/**
 * Created by Kun Yang on 2018/8/20.
 */
public class ProtoExMessageHeaderTest  extends MessageHeaderTest {

    @Override
    public MessageHeader create(long id, int protocol, int code, long time, long toMessage, Object head) {
        return new ProtoExMessageHeader()
                .setId(id)
                .setCode(code)
                .setTime(time)
                .setProtocol(protocol)
                .setToMessage(toMessage)
                .setAttachment(head);
    }
}