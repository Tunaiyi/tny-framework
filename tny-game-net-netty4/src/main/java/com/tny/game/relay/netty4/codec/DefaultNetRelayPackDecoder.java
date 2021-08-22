package com.tny.game.relay.netty4.codec;

import com.tny.game.common.enums.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.netty4.codec.*;
import com.tny.game.relay.*;
import com.tny.game.relay.packet.*;
import com.tny.game.relay.packet.arguments.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.FastThreadLocal;
import org.slf4j.Logger;

import static com.tny.game.relay.RelayCodecConstants.*;
import static org.slf4j.LoggerFactory.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/6 8:46 下午
 */
public class DefaultNetRelayPackDecoder extends BaseRelayPackCodec implements NetRelayPackDecoder {

	public static final Logger LOGGER = getLogger(DefaultNetRelayPackDecoder.class);

	private static final FastThreadLocal<byte[]> MAGICS_LOCAL = new FastThreadLocal<byte[]>() {

		@Override
		protected byte[] initialValue() {
			return new byte[RelayCodecConstants.RELAY_MAGIC.length];
		}
	};

	private final RelayPacketArgumentsCodecorManager relayPacketArgumentsCodecorManager;

	public DefaultNetRelayPackDecoder(RelayPacketArgumentsCodecorManager relayPacketArgumentsCodecorManager) {
		this.relayPacketArgumentsCodecorManager = relayPacketArgumentsCodecorManager;
	}

	@Override
	public Object decodeObject(ChannelHandlerContext ctx, ByteBuf in) {
		in.markReaderIndex();
		if (in.readableBytes() < RELAY_PACKET_HEAD_LENGTH_BYTES_SIZE) {
			return null;
		}
		final byte[] magics = MAGICS_LOCAL.get();
		in.readBytes(magics);
		if (!isMagic(magics)) {
			throw CodecException.causeDecode("illegal relay magics");
		}
		int packetLength = NettyVarIntCoder.readFixed32(in);
		if (in.readableBytes() < packetLength) {
			in.resetReaderIndex();
			return null;
		}
		if (packetLength != 0) {
			ByteBuf packetBody = null;
			Class<?> argumentsClass = null;
			try {
				RelayPacketArguments arguments;
				packetBody = in.readBytes(packetLength);
				byte option = in.readByte();
				RelayPacketType relayType = EnumAide.check(RelayPacketType.class, (byte)(option & RELAY_PACKET_TYPE_MASK));
				long tunnelId = NettyVarIntCoder.readFixed64(packetBody);
				long time = NettyVarIntCoder.readFixed64(packetBody);
				argumentsClass = relayType.getArgumentsClass();
				RelayPacketArgumentsCodecor<RelayPacketArguments> codecor = relayPacketArgumentsCodecorManager.codecor(argumentsClass);
				arguments = codecor.decode(ctx, packetBody);
				return relayType.createPacket(tunnelId, arguments, time);
			} catch (Exception e) {
				throw CodecException.causeDecode(e, "decode {} exception", argumentsClass);
			} finally {
				ReferenceCountUtil.release(packetBody);
			}
		} else {
			throw CodecException.causeDecode("packetLength is null");
		}
	}

}
