package com.tny.game.net.netty4.network.codec;

import com.tny.game.common.runtime.*;
import com.tny.game.net.base.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.*;

import java.nio.ByteBuffer;

import static com.tny.game.net.base.NetLogger.*;

public class DatagramPackEncodeHandler extends MessageToByteEncoder<Object> implements DatagramPackCodecErrorHandler {

	/**
	 * 日志
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(NetLogger.CODER);

	private final DatagramPackEncoder encoder;

	private final boolean closeOnError;

	public DatagramPackEncodeHandler(DatagramPackEncoder encoder, boolean closeOnError) {
		this.encoder = encoder;
		this.closeOnError = closeOnError;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
		try (ProcessTracer ignored = MESSAGE_ENCODE_WATCHER.trace()) {
			if (msg instanceof ByteBuf) {
				out.writeBytes((ByteBuf)msg);
				return;
			} else if (msg instanceof byte[]) {
				out.writeBytes((byte[])msg);
				return;
			}
			if (msg instanceof ByteBuffer) {
				out.writeBytes((ByteBuffer)msg);
				return;
			}
			if (msg instanceof Message) {
				try {
					this.encoder.encodeObject(ctx, (Message)msg, out);
				} catch (Throwable e) {
					handleOnEncodeError(LOGGER, ctx, e, closeOnError);
				}
				return;
			}
		}
		throw NetCodecException.causeEncodeFailed("can not encode {}", msg.getClass());
	}

}
