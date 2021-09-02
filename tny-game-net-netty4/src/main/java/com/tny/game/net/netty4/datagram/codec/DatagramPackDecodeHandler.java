package com.tny.game.net.netty4.datagram.codec;

import com.tny.game.common.context.*;
import com.tny.game.common.runtime.*;
import com.tny.game.net.base.*;
import com.tny.game.net.message.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.*;

import java.util.List;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.net.base.NetLogger.*;

public class DatagramPackDecodeHandler extends ByteToMessageDecoder implements DatagramPackCodecErrorHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(NetLogger.CODER);

	private final DatagramPackDecoder decoder;

	private final boolean closeOnError;

	private final DatagramPackDecodeMarker marker = new DatagramPackDecodeMarker();

	public DatagramPackDecodeHandler(DatagramPackDecoder decoder) {
		this(decoder, false);
	}

	public DatagramPackDecodeHandler(DatagramPackDecoder decoder, boolean closeOnError) {
		this.decoder = decoder;
		this.closeOnError = closeOnError;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
		try {
			try (ProcessTracer ignored = MESSAGE_DECODE_WATCHER.trace()) {
				while (in.readableBytes() > 0) {
					ProcessTracer allTrace = NET_TRACE_ALL_WATCHER.trace();
					ProcessTracer handleTrace = NET_TRACE_INPUT_ALL.trace();
					ProcessTracer toTunnelTrace = NET_TRACE_INPUT_READ_TO_TUNNEL_WATCHER.trace();
					Object messageObject = this.decoder.decodeObject(ctx, in, this.marker);
					if (messageObject instanceof Message) {
						Message message = as(messageObject);
						if (message.getType() == MessageType.MESSAGE) {
							Attributes attributes = (message).attributes();
							attributes.setAttribute(NET_TRACE_ALL_ATTR_KEY, allTrace);
							attributes.setAttribute(NET_TRACE_INPUT_ALL_ATTR_KEY, handleTrace);
							attributes.setAttribute(NET_TRACE_INPUT_READ_TO_TUNNEL_ATTR_KEY, toTunnelTrace);
						}
						out.add(messageObject);
					} else {
						break;
					}
				}
			}
		} catch (Throwable exception) {
			handleOnEncodeError(LOGGER, ctx, exception, closeOnError);
		}
	}

}
