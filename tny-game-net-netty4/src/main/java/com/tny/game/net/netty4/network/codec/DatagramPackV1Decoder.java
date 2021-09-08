package com.tny.game.net.netty4.network.codec;

import com.tny.game.net.base.*;
import com.tny.game.net.codec.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.netty4.network.*;
import com.tny.game.net.transport.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.FastThreadLocal;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.net.message.CodecConstants.*;

public class DatagramPackV1Decoder extends DatagramPackV1Codec implements DatagramPackDecoder {

	public DatagramPackV1Decoder(DatagramPackCodecSetting config) {
		super(config);
	}

	public DatagramPackV1Decoder() {
	}

	private static final FastThreadLocal<byte[]> MAGICS_LOCAL = new FastThreadLocal<byte[]>() {

		@Override
		protected byte[] initialValue() {
			return new byte[CodecConstants.FRAME_MAGIC.length];
		}
	};

	@Override
	public Message decodeObject(ChannelHandlerContext ctx, ByteBuf in, DatagramPackDecodeMarker marker) throws Exception {
		Channel channel = ctx.channel();
		byte option;
		int payloadLength;
		if (marker.isMark()) {
			option = marker.getOption();
			payloadLength = marker.getPayloadLength();
		} else {
			// 获取打包器
			if (in.readableBytes() < CodecConstants.FRAME_MAGIC.length + CodecConstants.OPTION_SIZE + CodecConstants.MESSAGE_LENGTH_SIZE) {
				return null;
			}
			final byte[] magics = MAGICS_LOCAL.get();
			in.readBytes(magics);
			if (!isMagic(magics)) {
				in.skipBytes(in.readableBytes());
				throw CodecException.causeDecodeError("illegal magics");
			}
			option = in.readByte();
			if (isOption(option, DATA_PACK_OPTION_MESSAGE_TYPE_MASK, DATA_PACK_OPTION_MESSAGE_TYPE_VALUE_PING)) {
				return TickMessage.ping();
			} else if (isOption(option, DATA_PACK_OPTION_MESSAGE_TYPE_MASK, DATA_PACK_OPTION_MESSAGE_TYPE_VALUE_PONG)) {
				return TickMessage.pong();
			}
			//            payloadLength = in.readInt();
			payloadLength = NettyVarIntCoder.readFixed32(in);
			if (payloadLength > config.getMaxPayloadLength()) {
				in.skipBytes(in.readableBytes());
				throw CodecException.causeDecodeError("decode message failed, because payloadLength {} > maxPayloadLength {}",
						payloadLength, config.getMaxPayloadLength());
			}
			marker.record(option, payloadLength);
		}
		// 读取请求信息体
		if (in.readableBytes() < payloadLength) {
			return null;
		}
		try {
			Message message = readPayload(channel, in, option, payloadLength);
			NetLogger.logReceive(() -> channel.attr(NettyNetAttrKeys.TUNNEL).get(), message);
			return message;
		} finally {
			marker.reset();
		}
	}

	private Message readPayload(Channel channel, ByteBuf in, byte option, int payloadLength) throws Exception {
		ByteBuf bodyBuffer = null;
		try {
			NetTunnel<?> tunnel = channel.attr(NettyNetAttrKeys.TUNNEL).get();
			// 获取打包器
			int index = in.readerIndex();
			long accessId = NettyVarIntCoder.readVarInt64(in);
			DataPackageContext packageContext = channel.attr(NettyNetAttrKeys.READ_PACKAGER).get();
			if (packageContext == null) {
				packageContext = new DataPackageContext(accessId, config);
				tunnel.setAccessId(accessId);
				channel.attr(NettyNetAttrKeys.READ_PACKAGER).set(packageContext);
			}
			int number = NettyVarIntCoder.readVarInt32(in);
			// 移动到当前包序号
			packageContext.goToAndCheck(number);
			boolean verifyEnable = isOption(option, DATA_PACK_OPTION_VERIFY, DATA_PACK_OPTION_VERIFY);
			if (config.isVerifyEnable() && !verifyEnable) {
				throw CodecException.causeDecodeError("packet need verify!");
			}
			boolean encryptEnable = isOption(option, DATA_PACK_OPTION_ENCRYPT, DATA_PACK_OPTION_ENCRYPT);
			if (config.isEncryptEnable() && !encryptEnable) {
				throw CodecException.causeDecodeError("packet need encrypt!");
			}
			boolean wasteBytesEnable = isOption(option, DATA_PACK_OPTION_WASTE_BYTES, DATA_PACK_OPTION_WASTE_BYTES);
			if (config.isWasteBytesEnable() && !wasteBytesEnable) {
				throw CodecException.causeDecodeError("packet need waste bytes!");
			}
			//        // 检测时间
			//        packager.checkPacketTime(time);
			//计算 body length
			NettyWasteReader reader = new NettyWasteReader(packageContext, wasteBytesEnable, config);
			int verifyLength = verifyEnable ? this.verifier.getCodeLength() : 0;
			int bodyLength = payloadLength - verifyLength - (in.readerIndex() - index);
			// 读取废字节中的 bodyBytes
			bodyBuffer = in.alloc().heapBuffer(bodyLength);
			logger.debug("in payloadIndex start {}", in.readerIndex());
			reader.read(in, bodyLength, bodyBuffer);
			logger.debug("in payloadIndex end {}", in.readerIndex());
			// 加密
			if (encryptEnable) {
				// TODO 是否需要重新创建 buffer
				this.crypto.decrypt(packageContext, bodyBuffer.array(), bodyBuffer.arrayOffset(), bodyBuffer.readableBytes());
				if (logger.isDebugEnabled()) {
					CodecLogger.logBinary(logger, "sendMessage body decryption |  body  {} ",
							bodyBuffer.array(), bodyBuffer.arrayOffset(), bodyBuffer.readableBytes());
				}
			}
			// 校验码验证
			if (verifyEnable) {
				byte[] verifyCode = new byte[verifyLength];
				in.readBytes(verifyCode);
				if (this.verifier.verify(packageContext, bodyBuffer.array(), bodyBuffer.arrayOffset(), bodyBuffer.readableBytes(), verifyCode)) {
					throw CodecException.causeVerify("packet verify failed");
				}
			}
			return this.messageCodec.decode(bodyBuffer, as(tunnel.getMessageFactory()));
		} finally {
			ReferenceCountUtil.release(bodyBuffer);
		}
	}

}
