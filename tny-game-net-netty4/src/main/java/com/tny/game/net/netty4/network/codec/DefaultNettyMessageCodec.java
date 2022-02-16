package com.tny.game.net.netty4.network.codec;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.codec.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.message.common.*;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.net.message.CodecConstants.*;
import static com.tny.game.net.message.MessageType.*;

@Unit
public class DefaultNettyMessageCodec implements NettyMessageCodec {

	public static final Logger LOGGER = LoggerFactory.getLogger(DefaultNettyMessageCodec.class);

	private final MessageBodyCodec<Object> bodyCoder;

	private final MessageRelayStrategy messageRelayStrategy;

	public DefaultNettyMessageCodec(MessageBodyCodec<?> bodyCoder) {
		this(bodyCoder, null);
	}

	public DefaultNettyMessageCodec(MessageBodyCodec<?> bodyCoder, MessageRelayStrategy relayStrategy) {
		this.bodyCoder = as(bodyCoder);
		this.messageRelayStrategy = ObjectAide.ifNull(relayStrategy, MessageRelayStrategy.NO_RELAY_STRATEGY);
	}

	@Override
	public NetMessage decode(ByteBuf buffer, MessageFactory messageFactory) throws Exception {
		long id = NettyVarIntCoder.readVarInt64(buffer);
		byte option = buffer.readByte();
		MessageMode mode = MessageMode.valueOf(MESSAGE, (byte)(option & MESSAGE_HEAD_OPTION_MODE_MASK));
		int protocol = NettyVarIntCoder.readVarInt32(buffer);
		int code = NettyVarIntCoder.readVarInt32(buffer);
		long toMessage = NettyVarIntCoder.readVarInt64(buffer);
		long time = NettyVarIntCoder.readVarInt64(buffer);
		Object body = null;
		int line = (byte)(option & MESSAGE_HEAD_OPTION_LINE_MASK);
		line = line >> MESSAGE_HEAD_OPTION_LINE_SHIFT;
		CommonMessageHead head = new CommonMessageHead(id, mode, line, protocol, code, toMessage, time);
		if (!CodecConstants.isOption(option, MESSAGE_HEAD_OPTION_EXIST_BODY_VALUE_EXIST, MESSAGE_HEAD_OPTION_EXIST_BODY_VALUE_EXIST)) {
			return messageFactory.create(head, null);
		}

		int length = NettyVarIntCoder.readVarInt32(buffer);
		ByteBuf bodyBuff = buffer.alloc().heapBuffer(length);
		buffer.readBytes(bodyBuff, length);
		if (this.messageRelayStrategy.isRelay(head)) {
			// WARN 不释放, 等待转发后释放
			body = new ByteBufMessageBody(bodyBuff, true);
		} else {
			try {
				body = this.bodyCoder.decode(bodyBuff);
			} finally {
				ReferenceCountUtil.release(bodyBuff);
			}
		}
		return messageFactory.create(head, body);
	}

	@Override
	public void encode(NetMessage message, ByteBuf buffer) throws Exception {
		if (message.getType() != MESSAGE) {
			return;
		}
		//		ProtoExOutputStream stream = new ProtoExOutputStream(1024, 2 * 1024);
		NettyVarIntCoder.writeVarInt64(message.getId(), buffer);
		MessageHead head = message.getHead();
		MessageMode mode = head.getMode();
		byte option = mode.getOption();
		option = (byte)(option | (message.existBody() ? CodecConstants.MESSAGE_HEAD_OPTION_EXIST_BODY_VALUE_EXIST : (byte)0));
		int line = head.getLine();
		if (line < MESSAGE_HEAD_OPTION_LINE_VALUE_MIN || line > MESSAGE_HEAD_OPTION_LINE_VALUE_MAX) {
			throw NetCodecException.causeEncodeFailed("line is {}. line must {} <= line <= {}", line,
					MESSAGE_HEAD_OPTION_LINE_VALUE_MIN, MESSAGE_HEAD_OPTION_LINE_VALUE_MAX);
		}
		option = (byte)(option | line << MESSAGE_HEAD_OPTION_LINE_SHIFT);
		buffer.writeByte(option);
		NettyVarIntCoder.writeVarInt32(head.getProtocolId(), buffer);
		NettyVarIntCoder.writeVarInt32(head.getCode(), buffer);
		NettyVarIntCoder.writeVarInt64(head.getToMessage(), buffer);
		NettyVarIntCoder.writeVarInt64(head.getTime(), buffer);
		if (message.existBody()) {
			Object body = message.bodyAs(Object.class);
			writeObject(buffer, head, body, this.bodyCoder);
		}
	}

	private void writeObject(ByteBuf buffer, MessageHead head, Object object, MessageBodyCodec<Object> coder) throws Exception {
		OctetMessageBody releaseBody = null;
		try {
			if (object instanceof byte[]) {
				write(buffer, (byte[])object);
			} else if (object instanceof ByteArrayMessageBody) {
				ByteArrayMessageBody arrayMessageBody = as(object);
				releaseBody = arrayMessageBody;
				byte[] data = arrayMessageBody.getBody();
				write(buffer, data);
			} else if (object instanceof ByteBufMessageBody) {
				ByteBufMessageBody messageBody = (ByteBufMessageBody)object;
				releaseBody = messageBody;
				ByteBuf data = messageBody.getBody();
				if (data == null) {
					throw NetCodecException.causeEncodeFailed("ByteBufMessageBody is released");
				}
				NettyVarIntCoder.writeVarInt32(data.readableBytes(), buffer);
				buffer.writeBytes(data);
			} else {
				ByteBuf bodyBuf = null;
				try {
					bodyBuf = buffer.alloc().heapBuffer();
					coder.encode(as(object), bodyBuf);
					NettyVarIntCoder.writeVarInt32(bodyBuf.readableBytes(), buffer);
					buffer.writeBytes(bodyBuf);
				} finally {
					ReferenceCountUtil.release(bodyBuf);
				}
			}
		} finally {
			OctetMessageBody.release(releaseBody);
		}
	}

	private void write(ByteBuf buffer, byte[] data) {
		NettyVarIntCoder.writeVarInt32(data.length, buffer);
		buffer.writeBytes(data);
	}

}
