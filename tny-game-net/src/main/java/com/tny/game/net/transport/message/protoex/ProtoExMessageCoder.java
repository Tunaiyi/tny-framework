package com.tny.game.net.transport.message.protoex;

import com.tny.game.net.transport.message.Message;
import com.tny.game.net.transport.message.coder.MessageCoder;
import com.tny.game.protoex.*;
import com.tny.game.protoex.annotations.TypeEncode;

public class ProtoExMessageCoder implements MessageCoder {

    public final static int MESSAGE_HEAD_ID = 1;

    @Override
    public Message<?> decode(final byte[] array) {
        ProtoExReader reader = new ProtoExReader(array);
        return reader.readMessage();
    }

    @Override
    public byte[] encode(Message<?> message) {
        ProtoExWriter writer = new ProtoExWriter();
        writer.writeMessage(message, TypeEncode.EXPLICIT);
        return writer.toByteArray();
    }

}
