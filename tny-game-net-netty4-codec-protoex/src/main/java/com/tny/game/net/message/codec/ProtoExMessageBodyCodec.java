package com.tny.game.net.message.codec;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.netty4.datagram.codec.*;
import com.tny.game.protoex.*;
import com.tny.game.protoex.annotations.*;
import io.netty.buffer.ByteBuf;
import org.slf4j.*;

@Unit
public class ProtoExMessageBodyCodec<T> implements MessageBodyCodec<T> {

	public static final Logger LOGGER = LoggerFactory.getLogger(ProtoExMessageBodyCodec.class);

	@Override
	public T decode(ByteBuf buffer) {
		try (ProtoExReader bodyReader = new ProtoExReader(new ProtoExInputStream(buffer.nioBuffer()))) {
			return bodyReader.readMessage();
		}
	}

	@Override
	public void encode(T object, ByteBuf code) {
		try (ProtoExWriter writer = new ProtoExWriter()) {
			if (object != null) {
				writer.writeMessage(object, TypeEncode.EXPLICIT);
			}
			code.writeBytes(writer.toByteArray());
		}
	}

}
