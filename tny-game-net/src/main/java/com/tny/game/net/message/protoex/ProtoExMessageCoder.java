package com.tny.game.net.message.protoex;

import com.tny.game.net.message.Message;
import com.tny.game.net.message.coder.MessageCoder;
import com.tny.game.protoex.ProtoExReader;
import com.tny.game.protoex.ProtoExWriter;
import com.tny.game.protoex.annotations.TypeEncode;

import java.io.IOException;

public class ProtoExMessageCoder implements MessageCoder {

    public final static int REQUEST_ID = 1;
    public final static int RESPONSE_ID = 2;
    public final static int MESSAGE_ID = 3;

    @Override
    public Message<?> decode(final byte[] array) throws Exception {
        ProtoExReader reader = new ProtoExReader(array);
        return reader.readMessage();
    }

    @Override
    public byte[] encode(Message<?> message) throws IOException {
        ProtoExWriter writer = new ProtoExWriter();
        writer.writeMessage(message, TypeEncode.EXPLICIT);
        return writer.toByteArray();
    }

}
