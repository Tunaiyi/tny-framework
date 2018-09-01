package com.tny.game.net.message.protoex;

import com.tny.game.net.message.*;

/**
 * Created by Kun Yang on 2018/8/20.
 */
public class ProtoExMessageHeaderTest  extends MessageHeaderTest {

    @Override
    public MessageHeader create(int id, int protocol, int code, long time, int toMessage, Object head) {
        return new ProtoExMessageHeader()
                .setId(id)
                .setCode(code)
                .setTime(time)
                .setProtocol(protocol)
                .setToMessage(toMessage)
                .setHead(head);
    }

}
