package com.tny.game.net.message.codec.protoex;

import com.tny.game.net.codec.*;
import com.tny.game.net.message.*;
import com.tny.game.net.message.codec.*;
import com.tny.game.net.message.common.*;
import com.tny.game.net.netty4.network.codec.*;
import com.tny.game.net.transport.*;
import com.tny.game.protoex.*;
import io.netty.buffer.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 */
public class MessageCodecTest {

	private ProtoExMessageBodyCodec<Object> objectCodec = new ProtoExMessageBodyCodec<>();

	private NettyMessageCodec codec = new DefaultNettyMessageCodec(
			this.objectCodec,
			MessageRelayStrategy.NO_RELAY_STRATEGY);

	private NettyMessageCodec codecNoDecode = new DefaultNettyMessageCodec(
			this.objectCodec,
			(head) -> false);

	private CommonMessageFactory messageFactory = new CommonMessageFactory();

	private NetMessage createMessage(Object body, Object tail) {
		long uid = 5000L;
		return this.messageFactory.create(1L, MessageContexts.<Long>request(Protocols.protocol(100_100), body));
	}

	@Test
	public void encodeDecode() throws Exception {
		TestMsgObject body = new TestMsgObject(100, "I am test body!");
		TestMsgObject attachment = new TestMsgObject(100, "I am test attachment!");
		ByteBuf data;
		ByteArrayMessageBody bodyBytes;
		// 正常解析 有 Body
		NetMessage encodeMessage = createMessage(body, attachment);
		data = ByteBufAllocator.DEFAULT.heapBuffer();
		data.markReaderIndex();
		this.codec.encode(encodeMessage, data);
		Message decodeMessage = this.codec.decode(data, this.messageFactory);
		assertEquals(encodeMessage.getHead(), decodeMessage.getHead());
		assertEquals(encodeMessage.bodyAs(TestMsgObject.class), decodeMessage.bodyAs(TestMsgObject.class));
		// 不解析 Body, 有 Body
		data.resetReaderIndex();
		Message noDecodeBodyMessage = this.codecNoDecode.decode(data, this.messageFactory);
		assertEquals(encodeMessage.getHead(), noDecodeBodyMessage.getHead());
		bodyBytes = noDecodeBodyMessage.bodyAs(ByteArrayMessageBody.class);
		assertNotNull(bodyBytes);
		ProtoExReader bodyReader = new ProtoExReader(bodyBytes.getBody());
		TestMsgObject realBody = bodyReader.readMessage(TestMsgObject.class);
		assertEquals(encodeMessage.bodyAs(TestMsgObject.class), realBody);
		// 正常解析 无 Body
		NetMessage encodeNoBodyMessage = createMessage(null, attachment);

		data.clear();
		this.codec.encode(encodeNoBodyMessage, data);
		Message decodeNoBodyMessage = this.codec.decode(data, this.messageFactory);
		assertEquals(encodeNoBodyMessage.getHead(), decodeNoBodyMessage.getHead());
		assertNull(decodeNoBodyMessage.bodyAs(TestMsgObject.class));
		// 不解析 Body, 无 Body
		Message noDecodeBodyNoBodyMessage = this.codecNoDecode.decode(data, this.messageFactory);
		assertEquals(encodeNoBodyMessage.getHead(), noDecodeBodyNoBodyMessage.getHead());
		bodyBytes = noDecodeBodyNoBodyMessage.bodyAs(ByteArrayMessageBody.class);
		assertNull(bodyBytes);
	}

}