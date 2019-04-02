package com.tny.game.net.message.protoex;

import com.tny.game.net.codec.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.message.common.*;
import com.tny.game.net.transport.*;
import com.tny.game.protoex.*;
import org.junit.Test;

import java.time.Instant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * <p>
 */
public class MessageCodecTest {

    private ProtoExCodec<Object> objectCodec = new ProtoExCodec<>();

    private MessageCodec<Long> codec = new DefaultMessageCodec<>(
            objectCodec,
            objectCodec,
            DecodeStrategy.DECODE_ALL_STRATEGY);


    private MessageCodec<Long> codecNoDecode = new DefaultMessageCodec<>(
            objectCodec,
            objectCodec,
            (head, tail) -> false);

    private CommonMessageFactory<Long> messageFactory = new CommonMessageFactory<>();

    private Message<Long> createMessage(Object body, Object tail) {
        long uid = 5000L;
        return messageFactory.create(1L, MessageContexts.<Long>request(ProtocolAide.protocol(100_100), body)
                        .setTail(tail),
                Certificates.createAutherized(123456L, uid, Instant.now()));
    }

    @Test
    public void encodeDecode() throws Exception {
        TestMsgOject body = new TestMsgOject(100, "I am test body!");
        TestMsgOject attachment = new TestMsgOject(100, "I am test attachment!");
        byte[] data;
        BodyBytes bodyBytes;


        // 正常解析 有 Body
        Message<Long> encodeMessage = createMessage(body, attachment);
        data = codec.encode(encodeMessage);

        Message<Long> decodeMessage = codec.decode(data, messageFactory);
        assertEquals(encodeMessage.getHead(), decodeMessage.getHead());
        assertEquals(encodeMessage.getBody(TestMsgOject.class), decodeMessage.getBody(TestMsgOject.class));

        // 不解析 Body, 有 Body
        Message<Long> noDecodeBodyMessage = codecNoDecode.decode(data, messageFactory);
        assertEquals(encodeMessage.getHead(), noDecodeBodyMessage.getHead());
        bodyBytes = noDecodeBodyMessage.getBody(BodyBytes.class);
        assertNotNull(bodyBytes);

        ProtoExReader bodyReader = new ProtoExReader(bodyBytes.getBodyBytes());
        TestMsgOject realBody = bodyReader.readMessage(TestMsgOject.class);
        assertEquals(encodeMessage.getBody(TestMsgOject.class), realBody);


        // 正常解析 无 Body
        Message<Long> encodeNoBodyMessage = createMessage(null, attachment);
        data = codec.encode(encodeNoBodyMessage);
        Message<Long> decodeNoBodyMessage = codec.decode(data, messageFactory);
        assertEquals(encodeNoBodyMessage.getHead(), decodeNoBodyMessage.getHead());
        assertNull(decodeNoBodyMessage.getBody(TestMsgOject.class));

        // 不解析 Body, 无 Body
        Message<Long> noDecodeBodyNoBodyMessage = codecNoDecode.decode(data, messageFactory);
        assertEquals(encodeNoBodyMessage.getHead(), noDecodeBodyNoBodyMessage.getHead());
        bodyBytes = noDecodeBodyNoBodyMessage.getBody(BodyBytes.class);
        assertNull(bodyBytes);
    }

}