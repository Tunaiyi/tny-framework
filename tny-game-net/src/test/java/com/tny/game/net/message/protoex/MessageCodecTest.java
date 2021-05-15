package com.tny.game.net.message.protoex;

import com.tny.game.net.codec.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.message.common.*;
import com.tny.game.protoex.*;
import org.junit.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 */
public class MessageCodecTest {

    private ProtoExCodec<Object> objectCodec = new ProtoExCodec<>();

    private MessageCodec<Long> codec = new DefaultMessageCodec<>(
            this.objectCodec,
            this.objectCodec,
            DecodeStrategy.DECODE_ALL_STRATEGY);

    private MessageCodec<Long> codecNoDecode = new DefaultMessageCodec<>(
            this.objectCodec,
            this.objectCodec,
            (head) -> false);

    private CommonMessageFactory<Long> messageFactory = new CommonMessageFactory<>();

    private Message createMessage(Object body, Object tail) {
        long uid = 5000L;
        return this.messageFactory.create(1L, MessageContexts.<Long>request(ProtocolAide.protocol(100_100), body)
                .setTail(tail));
    }

    @Test
    public void encodeDecode() throws Exception {
        TestMsgOject body = new TestMsgOject(100, "I am test body!");
        TestMsgOject attachment = new TestMsgOject(100, "I am test attachment!");
        byte[] data;
        BodyBytes bodyBytes;

        // 正常解析 有 Body
        Message encodeMessage = createMessage(body, attachment);
        data = this.codec.encode(encodeMessage);

        Message decodeMessage = this.codec.decode(data, this.messageFactory);
        assertEquals(encodeMessage.getHead(), decodeMessage.getHead());
        assertEquals(encodeMessage.getBody(TestMsgOject.class), decodeMessage.getBody(TestMsgOject.class));

        // 不解析 Body, 有 Body
        Message noDecodeBodyMessage = this.codecNoDecode.decode(data, this.messageFactory);
        assertEquals(encodeMessage.getHead(), noDecodeBodyMessage.getHead());
        bodyBytes = noDecodeBodyMessage.getBody(BodyBytes.class);
        assertNotNull(bodyBytes);

        ProtoExReader bodyReader = new ProtoExReader(bodyBytes.getBodyBytes());
        TestMsgOject realBody = bodyReader.readMessage(TestMsgOject.class);
        assertEquals(encodeMessage.getBody(TestMsgOject.class), realBody);

        // 正常解析 无 Body
        Message encodeNoBodyMessage = createMessage(null, attachment);
        data = this.codec.encode(encodeNoBodyMessage);
        Message decodeNoBodyMessage = this.codec.decode(data, this.messageFactory);
        assertEquals(encodeNoBodyMessage.getHead(), decodeNoBodyMessage.getHead());
        assertNull(decodeNoBodyMessage.getBody(TestMsgOject.class));

        // 不解析 Body, 无 Body
        Message noDecodeBodyNoBodyMessage = this.codecNoDecode.decode(data, this.messageFactory);
        assertEquals(encodeNoBodyMessage.getHead(), noDecodeBodyNoBodyMessage.getHead());
        bodyBytes = noDecodeBodyNoBodyMessage.getBody(BodyBytes.class);
        assertNull(bodyBytes);
    }

}