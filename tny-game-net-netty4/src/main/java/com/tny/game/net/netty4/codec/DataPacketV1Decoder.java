package com.tny.game.net.netty4.codec;

import com.tny.game.net.codec.*;
import com.tny.game.net.codec.v1.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.message.codec.*;
import com.tny.game.net.netty4.*;
import com.tny.game.net.transport.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.util.concurrent.FastThreadLocal;
import org.slf4j.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.net.message.codec.CodecConstants.*;

public class DataPacketV1Decoder extends DataPacketV1BaseCodec implements DataPacketDecoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataPacketV1Decoder.class);

    public DataPacketV1Decoder(DataPacketV1Config config) {
        super(config);
    }

    public DataPacketV1Decoder() {
    }

    private static final FastThreadLocal<byte[]> MAGICS_LOCAL = new FastThreadLocal<byte[]>() {
        @Override
        protected byte[] initialValue() {
            return new byte[CodecConstants.FRAME_MAGIC.length];
        }
    };

    @Override
    public Message decodeObject(ChannelHandlerContext ctx, ByteBuf in, DataPacketMarker marker) throws Exception {
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
                throw CodecException.causeDecode("illegal magics");
            }

            option = in.readByte();

            if (isOption(option, DATA_PACK_OPTION_MESSAGE_TYPE_MASK, DATA_PACK_OPTION_MESSAGE_TYPE_VALUE_PING)) {
                return TickMessage.ping();
            } else if (isOption(option, DATA_PACK_OPTION_MESSAGE_TYPE_MASK, DATA_PACK_OPTION_MESSAGE_TYPE_VALUE_PONG)) {
                return TickMessage.pong();
            }

            //            payloadLength = in.readInt();
            payloadLength = NettyVarIntCoder.readFixed32(in);

            if (payloadLength > this.config.getMaxPayloadLength()) {
                throw CodecException.causeDecode("decode message failed, because payloadLength {} > maxPayloadLength {}", payloadLength,
                        this.config.getMaxPayloadLength());
            }
            marker.record(option, payloadLength);
        }
        // 读取请求信息体
        if (in.readableBytes() < payloadLength) {
            return null;
        }
        try {
            return readPayload(channel, in, option, payloadLength);
        } finally {
            marker.reset();
        }
    }

    private Message readPayload(Channel channel, ByteBuf in, byte option, int payloadLength) throws Exception {

        byte[] bodyBytes;
        NetTunnel<?> tunnel = channel.attr(NettyAttrKeys.TUNNEL).get();

        // 获取打包器
        int index = in.readerIndex();
        long accessId = NettyVarIntCoder.readVarInt64(in);
        DataPackageContext packageContext = channel.attr(NettyAttrKeys.READ_PACKAGER).get();
        if (packageContext == null) {
            packageContext = new DataPackageContext(accessId, this.config);
            tunnel.setAccessId(accessId);
            channel.attr(NettyAttrKeys.READ_PACKAGER).set(packageContext);
        }

        int number = NettyVarIntCoder.readVarInt32(in);

        // 移动到当前包序号
        packageContext.goToAndCheck(number);

        boolean verifyEnable = isOption(option, DATA_PACK_OPTION_VERIFY, DATA_PACK_OPTION_VERIFY);
        if (this.config.isVerifyEnable() && !verifyEnable) {
            throw CodecException.causeDecode("packet need verify!");
        }
        boolean encryptEnable = isOption(option, DATA_PACK_OPTION_ENCRYPT, DATA_PACK_OPTION_ENCRYPT);
        if (this.config.isEncryptEnable() && !encryptEnable) {
            throw CodecException.causeDecode("packet need encrypt!");
        }
        boolean wasteBytesEnable = isOption(option, DATA_PACK_OPTION_WASTE_BYTES, DATA_PACK_OPTION_WASTE_BYTES);
        if (this.config.isWasteBytesEnable() && !wasteBytesEnable) {
            throw CodecException.causeDecode("packet need waste bytes!");
        }

        //        // 检测时间
        //        packager.checkPacketTime(time);

        //计算 body length
        NettyWasteReader reader = new NettyWasteReader(packageContext, wasteBytesEnable, this.config);
        int verifyLength = verifyEnable ? this.verifier.getCodeLength() : 0;
        int bodyLength = payloadLength - verifyLength - (in.readerIndex() - index);
        // 读取废字节中的 bodyBytes
        LOGGER.debug("in payloadIndex start {}", in.readerIndex());
        bodyBytes = reader.read(in, bodyLength);
        LOGGER.debug("in payloadIndex end {}", in.readerIndex());

        // 加密
        if (encryptEnable) {
            bodyBytes = this.crypto.decrypt(packageContext, bodyBytes);
            CodecLogger.logBinary(LOGGER, "sendMessage body decryption |  body  {} ", bodyBytes);
        }

        // 加密
        if (verifyEnable) {
            byte[] verifyCode = new byte[verifyLength];
            in.readBytes(verifyCode);
            if (this.verifier.verify(packageContext, bodyBytes, verifyCode)) {
                throw CodecException.causeVerify("packet verify failed");
            }
        }

        return this.messageCodec.decode(bodyBytes, as(tunnel.getMessageFactory()));
    }

    public static void main(String[] args) {

    }

}
