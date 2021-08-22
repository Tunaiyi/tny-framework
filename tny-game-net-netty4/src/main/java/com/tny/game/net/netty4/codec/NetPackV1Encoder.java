package com.tny.game.net.netty4.codec;

import com.tny.game.net.base.*;
import com.tny.game.net.codec.*;
import com.tny.game.net.message.*;
import com.tny.game.net.netty4.*;
import com.tny.game.net.transport.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.net.message.CodecConstants.*;

public class NetPackV1Encoder extends NetPackV1Codec implements NetPackEncoder {

	public static final Logger LOGGER = LoggerFactory.getLogger(NetPackV1Encoder.class);

	private int bufferSize;

	public NetPackV1Encoder() {
		super();
	}

	public NetPackV1Encoder(DataPacketCodecSetting config) {
		super(config);
	}

	@Override
	public void encodeObject(ChannelHandlerContext ctx, Message message, ByteBuf out) {
		Channel channel = ctx.channel();
		try {
			out.writeBytes(FRAME_MAGIC);
			MessageType messageType = message.getMode().getType();
			if (messageType != MessageType.MESSAGE) {
				out.writeByte(messageType.getOption());
				// out.writeInt(0); // BodySize
				return;
			}
			// 获取打包器
			DataPackageContext packageContext = channel.attr(NettyNetAttrKeys.WRITE_PACKAGER).get();
			NetTunnel<?> tunnel;
			if (packageContext == null) {
				tunnel = channel.attr(NettyNetAttrKeys.TUNNEL).get();
				packageContext = new DataPackageContext(tunnel.getAccessId(), config);
				channel.attr(NettyNetAttrKeys.WRITE_PACKAGER).set(packageContext);
			}
			NetLogger.logSend(() -> channel.attr(NettyNetAttrKeys.TUNNEL).get(), message);
			writePayload(packageContext, message, out);
		} catch (Exception e) {
			LOGGER.error("编码 message {} 异常", message, e);
		}
	}

	private void writePayload(DataPackageContext packager, Message message, ByteBuf out) throws Exception {
		ByteBuf bodyBuffer = null;
		try {
			// 写入 Option
			byte option = message.getMode().getType().getOption();
			option = CodecConstants.setOption(option, DATA_PACK_OPTION_VERIFY, config.isVerifyEnable());
			option = CodecConstants.setOption(option, DATA_PACK_OPTION_ENCRYPT, config.isEncryptEnable());
			option = CodecConstants.setOption(option, DATA_PACK_OPTION_WASTE_BYTES, config.isWasteBytesEnable());
			out.writeByte(option);
			int payloadLength = 0;
			// accessId
			long accessId = packager.getAccessId();
			payloadLength += NettyVarIntCoder.varInt64Size(accessId);
			// number
			int number = packager.nextNumber();
			payloadLength += NettyVarIntCoder.varInt2Size(number);
			// 计算废字节
			NettyWasteWriter wasteWriter = new NettyWasteWriter(packager, config);
			payloadLength += wasteWriter.getTotalWasteByteSize();
			// 包头
			bodyBuffer = out.alloc().buffer(bufferSize);
			this.messageCodec.encode(as(message), bodyBuffer);
			byte[] verifyCodeBytes = new byte[0];
			if (config.isVerifyEnable()) {
				// 生成校验码
				payloadLength += this.verifier.getCodeLength();
				verifyCodeBytes = this.verifier.generate(packager, bodyBuffer.array(), bodyBuffer.arrayOffset(), bodyBuffer.readableBytes());
			}
			LOGGER.debug("sendMessage : accessId {} | number {} | randCode {} | packLength {} | wasteBitSize {} | verify {}", accessId, number,
					packager.getPacketCode(), payloadLength, wasteWriter.getWasteBitSize(), config.isVerifyEnable());
			// 加密
			if (config.isEncryptEnable()) {
				// TODO 是否需要重新创建 buffer
				this.crypto.encrypt(packager, bodyBuffer.array(), bodyBuffer.arrayOffset(), bodyBuffer.readableBytes());
				if (LOGGER.isDebugEnabled()) {
					CodecLogger.logBinary(LOGGER, "sendMessage body decryption |  body  {} ",
							bodyBuffer.array(), bodyBuffer.arrayOffset(), bodyBuffer.readableBytes());
				}
			}
			// 包体长度
			// payloadLength += NettyVarintCoder.varint32Size(body.length);
			payloadLength += bodyBuffer.readableBytes();
			if (payloadLength > config.getMaxPayloadLength()) {
				LOGGER.warn("encode message {} failed payloadLength {} > maxPayloadLength {}", message, payloadLength, PAYLOAD_BYTES_MAX_SIZE);
			}
			// 写入包长度
			//        out.writeInt(payloadLength);
			NettyVarIntCoder.writeFixed32(payloadLength, out);
			// 写入 accessId
			NettyVarIntCoder.writeVarInt64(accessId, out);
			// 写入 number
			NettyVarIntCoder.writeVarInt32(number, out);
			LOGGER.debug("out payloadIndex start {}", out.writerIndex());
			// 写入包体长度 包体
			wasteWriter.write(out, bodyBuffer);
			LOGGER.debug("out payloadIndex end {}", out.writerIndex());
			// 校验码
			if (verifyCodeBytes != null) {
				out.writeBytes(verifyCodeBytes);
			}
		} finally {
			ReferenceCountUtil.release(bodyBuffer);
		}
	}

}
