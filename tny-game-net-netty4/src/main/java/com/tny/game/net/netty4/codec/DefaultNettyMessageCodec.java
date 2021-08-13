package com.tny.game.net.netty4.codec;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.codec.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.message.common.*;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.net.message.CodecConstants.*;
import static com.tny.game.net.message.MessageType.*;

@Unit
public class DefaultNettyMessageCodec implements NettyMessageCodec {

	private final MessageBodyCodec<Object> bodyCoder;

	//    private final Codec<?> tailCoder;
	private final RelayStrategy relayStrategy;

	public DefaultNettyMessageCodec(MessageBodyCodec<?> bodyCoder) {
		this(bodyCoder, null);
	}

	public DefaultNettyMessageCodec(MessageBodyCodec<?> bodyCoder, RelayStrategy relayStrategy) {
		this.bodyCoder = as(bodyCoder);
		this.relayStrategy = ObjectAide.ifNull(relayStrategy, RelayStrategy.NO_RELAY_STRATEGY);
	}

	@Override
	public Message decode(ByteBuf buffer, MessageFactory messageFactory) throws Exception {
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
		if (CodecConstants.isOption(option, MESSAGE_HEAD_OPTION_EXIST_BODY_VALUE_EXIST, MESSAGE_HEAD_OPTION_EXIST_BODY_VALUE_EXIST)) {
			int length = NettyVarIntCoder.readVarInt32(buffer);
			if (this.relayStrategy.isRelay(head)) {
				// WARN 不释放, 等待释放
				ByteBuf bodyBuff = buffer.alloc().heapBuffer(length);
				buffer.readBytes(bodyBuff, length);
				body = new ByteBufMessageBody(bodyBuff);
			} else {
				ByteBuf bodyBuff = buffer.copy(buffer.readerIndex(), length);
				body = this.bodyCoder.decode(bodyBuff);
			}
		}
		return messageFactory.create(head, body);
	}

	@Override
	public void encode(Message message, ByteBuf buffer) throws Exception {
		if (message.getMode().getType() != MESSAGE) {
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
			throw CodecException.causeEncode("line is {}. line must {} <= line <= {}", line,
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
			writeObject(buffer, body, this.bodyCoder);
		}
	}

	private void writeObject(ByteBuf buffer, Object object, MessageBodyCodec<Object> coder) throws Exception {
		if (object instanceof byte[]) {
			write(buffer, (byte[])object);
		} else if (object instanceof BytesArrayMessageBody) {
			byte[] data = ((BytesArrayMessageBody)object).getBodyBytes();
			write(buffer, data);
		} else if (object instanceof ByteBufMessageBody) {
			ByteBufMessageBody messageBody = (ByteBufMessageBody)object;
			try {
				ByteBuf data = messageBody.getBodyBytes();
				if (data == null) {
					throw CodecException.causeEncode("ByteBufMessageBody is released");
				}
				buffer.writeBytes(data);
			} finally {
				ReferenceCountUtil.release(messageBody);
			}
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
	}

	private void write(ByteBuf buffer, byte[] data) {
		NettyVarIntCoder.writeVarInt32(data.length, buffer);
		buffer.writeBytes(data);
	}

}
