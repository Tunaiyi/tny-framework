package com.tny.game.net.message.protoex;

import com.tny.game.net.message.*;
import com.tny.game.net.message.common.BodyBytes;
import com.tny.game.net.transport.*;
import com.tny.game.protoex.ProtoExReader;
import org.junit.*;

import java.time.Instant;

import static org.junit.Assert.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-18 16:56
 */
public class ProtoExCodecTest {

    private ProtoExCodec<Long> codec = new ProtoExCodec<>();

    private ProtoExCodec<Long> codecNoDecode = new ProtoExCodec<Long>() {

        @Override
        protected boolean isDecode(MessageHeader header) {
            return false;
        }

    };

    private long uid = 5000L;

    private ProtoExMessageFactory<Long> factory = new ProtoExMessageFactory<>();

    private Message<Long> createMessage(Object body, Object attachment) {
        return factory.create(1L, MessageContexts.<Long>request(ProtocolAide.protocol(100_100), body)
                        .setAttachment(attachment),
                Certificates.createAutherized(123456L, uid, Instant.now()));
    }

    @Test
    public void encodeDecode() {
        TestMsgOject body = new TestMsgOject(100, "I am test body!");
        TestMsgOject attachment = new TestMsgOject(100, "I am test attachment!");
        byte[] data = null;
        BodyBytes bodyBytes;


        // 正常解析 有 Body
        Message<Long> encodeMessage = createMessage(body, attachment);
        data = codec.encode(encodeMessage);

        Message<Long> decodeMessage = codec.decode(data);
        assertEquals(encodeMessage.getHeader(), decodeMessage.getHeader());
        assertEquals(encodeMessage.getBody(TestMsgOject.class), decodeMessage.getBody(TestMsgOject.class));

        // 不解析 Body, 有 Body
        Message<Long> noDecodeBodyMessage = codecNoDecode.decode(data);
        assertEquals(encodeMessage.getHeader(), noDecodeBodyMessage.getHeader());
        bodyBytes = noDecodeBodyMessage.getBody(BodyBytes.class);
        assertNotNull(bodyBytes);

        ProtoExReader bodyReader = new ProtoExReader(bodyBytes.getBodyBytes());
        TestMsgOject realBody = bodyReader.readMessage(TestMsgOject.class);
        assertEquals(encodeMessage.getBody(TestMsgOject.class), realBody);


        // 正常解析 无 Body
        Message<Long> encodeNoBodyMessage = createMessage(null, attachment);
        data = codec.encode(encodeNoBodyMessage);
        Message<Long> decodeNoBodyMessage = codec.decode(data);
        assertEquals(encodeNoBodyMessage.getHeader(), decodeNoBodyMessage.getHeader());
        assertNull(decodeNoBodyMessage.getBody(TestMsgOject.class));

        // 不解析 Body, 无 Body
        Message<Long> noDecodeBodyNoBodyMessage = codecNoDecode.decode(data);
        assertEquals(encodeNoBodyMessage.getHeader(), noDecodeBodyNoBodyMessage.getHeader());
        bodyBytes = noDecodeBodyNoBodyMessage.getBody(BodyBytes.class);
        assertNull(bodyBytes);
    }

}