package com.tny.game.net.dispatcher.message.protoex;

import com.tny.game.net.coder.MessageBodyCoder;
import com.tny.game.protoex.ProtoExReader;
import com.tny.game.protoex.ProtoExWriter;
import com.tny.game.protoex.annotations.TypeEncode;

import java.io.IOException;

public class ProtoExMessageCoder implements MessageBodyCoder {

    public final static int REQUSET_ID = 1;
    public final static int RESPONSE_ID = 2;

    @Override
    public Object doDecoder(final byte[] array, boolean isRequset) throws Exception {
        ProtoExReader reader = new ProtoExReader(array);
        return reader.readMessage();
    }

    @Override
    public byte[] doEncode(Object message) throws IOException {
        ProtoExWriter writer = new ProtoExWriter();
        writer.writeMessage(message, TypeEncode.EXPLICIT);
        byte[] data = writer.toByteArray();
        return data;
    }

}
