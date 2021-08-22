package com.tny.game.relay.netty4.codec;

import com.tny.game.net.netty4.codec.*;
import com.tny.game.relay.packet.*;
import com.tny.game.relay.packet.arguments.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;

import static com.tny.game.relay.RelayCodecConstants.*;
import static org.slf4j.LoggerFactory.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/6 8:46 下午
 */
public class DefaultNetRelayPackEncoder extends BaseRelayPackCodec implements NetRelayPackEncoder {

	public static final Logger LOGGER = getLogger(DefaultNetRelayPackEncoder.class);

	private final RelayPacketArgumentsCodecorManager relayPacketArgumentsCodecorManager;

	public DefaultNetRelayPackEncoder(RelayPacketArgumentsCodecorManager relayPacketArgumentsCodecorManager) {
		this.relayPacketArgumentsCodecorManager = relayPacketArgumentsCodecorManager;
	}

	@Override
	public void encodeObject(ChannelHandlerContext ctx, RelayPacket<?> relay, ByteBuf out) {
		try {
			out.writeBytes(RELAY_MAGIC);
			RelayPacketType relayType = relay.getType();
			// 转发包字节开始的 index
			int lengthIndex = out.writerIndex();
			// 跳过长度 4 字节
			out.writerIndex(lengthIndex + RELAY_PACKET_ARGUMENTS_LENGTH_BYTES_SIZE);
			int packetBytesStartIndex = out.writerIndex();
			// 写入转发包类型
			out.writeByte(relayType.getOption());
			// 写入转发包通道id 4 字节
			NettyVarIntCoder.writeFixed64(relay.getTunnelId(), out);
			// 写入转发包发送时间 (当前时间 - pipe open 时间)
			NettyVarIntCoder.writeFixed64((relay.getTime()), out);
			// 写入转发包参数
			RelayPacketArguments arguments = relay.getArguments();
			if (arguments != null) {
				RelayPacketArgumentsCodecor<RelayPacketArguments> codecor = relayPacketArgumentsCodecorManager.codecor(arguments.getClass());
				if (codecor != null) {
					codecor.encode(ctx, arguments, out);
				}
			}
			out.markWriterIndex();
			// 计算写入长度
			int packetBytesLength = out.writerIndex() - packetBytesStartIndex;
			// 跳到 lengthIndex 写入长度
			out.writerIndex(lengthIndex);
			NettyVarIntCoder.writeFixed32(packetBytesLength, out);
			// 返回写入末尾
			out.resetWriterIndex();
		} catch (Exception e) {
			LOGGER.error("编码 relay packet {} 异常", relay, e);
		}
	}

}
