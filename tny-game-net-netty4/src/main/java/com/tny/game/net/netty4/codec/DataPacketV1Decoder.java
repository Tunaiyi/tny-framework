package com.tny.game.net.netty4.codec;

import com.tny.game.net.codec.*;
import com.tny.game.net.codec.v1.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.message.coder.*;
import com.tny.game.net.netty4.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.net.message.coder.CodecContent.*;

public class DataPacketV1Decoder extends DataPacketV1BaseCodec implements DataPacketDecoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataPacketV1Decoder.class);

    public DataPacketV1Decoder(DataPacketV1Config config) {
        super(config);
    }

    public DataPacketV1Decoder() {
    }

    @Override
    public Message<?> decodeObject(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Channel channel = ctx.channel();
        // 获取打包器
        if (in.readableBytes() < CodecContent.FRAME_MAGIC.length + CodecContent.OPTION_SIZE + CodecContent.MESSAGE_LENGTH_SIZE)
            return null;
        in.markReaderIndex();
        final byte[] magics = new byte[CodecContent.FRAME_MAGIC.length];
        in.readBytes(magics);
        if (!isMagic(magics)) {
            in.resetReaderIndex();
            throw CodecException.causeDecode("illegal magics");
        }

        final byte option = in.readByte();

        if (isOption(option, DATA_PACK_OPTION_PING)) {
            return DetectMessage.ping();
        } else if (isOption(option, DATA_PACK_OPTION_PONG)) {
            return DetectMessage.pong();
        }

        int payloadLength = in.readInt();
        // 读取请求信息体
        if (in.readableBytes() < payloadLength) {
            in.resetReaderIndex();
            return null;
        }

        return readPayload(channel, in, option, payloadLength);
    }


    private Message<?> readPayload(Channel channel, ByteBuf in, byte option, int payloadLength) throws Exception {

        // 获取打包器
        int index = in.readerIndex();
        long accessId = NettyVarintCoder.readVarint64(in);
        DataPackager packager = channel.attr(NettyAttrKeys.READ_PACKAGER).get();
        NettyTunnel<?, ?> tunnel = channel.attr(NettyAttrKeys.TUNNEL).get();
        if (packager == null) {
            packager = new DataPackager(accessId, config);
            tunnel.setAccessId(accessId);
            channel.attr(NettyAttrKeys.READ_PACKAGER).set(packager);
        }

        int number = NettyVarintCoder.readVarint32(in);
        long time = NettyVarintCoder.readVarint64(in);

        // 移动到当前包序号
        packager.goToAndCheck(number);

        boolean verifyEnable = isOption(option, OPTION_VERIFY);
        if (config.isVerifyEnable() && !verifyEnable)
            throw CodecException.causeDecode("packet need verify!");
        boolean encryptEnable = isOption(option, OPTION_ENCRYPT);
        if (config.isEncryptEnable() && !encryptEnable)
            throw CodecException.causeDecode("packet need encrypt!");
        boolean wasteBytesEnable = isOption(option, OPTION_WASTE_BYTES);
        if (config.isWasteBytesEnable() && !wasteBytesEnable)
            throw CodecException.causeDecode("packet need waste bytes!");

        // 检测时间
        packager.checkPacketTime(time);

        //计算 body length
        NettyWasteReader reader = new NettyWasteReader(packager, wasteBytesEnable, config);
        int verifyLength = verifyEnable ? verifier.getCodeLength() : 0;
        int bodyLength = payloadLength - verifyLength - (in.readerIndex() - index);
        // 读取废字节中的 bodyBytes
        LOGGER.debug("in payloadIndex start {}", in.readerIndex());
        byte[] bodyBytes = reader.read(in, bodyLength);
        LOGGER.debug("in payloadIndex end {}", in.readerIndex());

        // 加密
        if (encryptEnable) {
            bodyBytes = crypto.decrypt(packager, bodyBytes);
            CodecLogger.logBinary(LOGGER, "sendMessage body decryption |  body  {} ", bodyBytes);
        }

        // 加密
        if (verifyEnable) {
            byte[] verifyCode = new byte[verifyLength];
            in.readBytes(verifyCode);
            if (verifier.verify(packager, bodyBytes, time, verifyCode))
                throw CodecException.causeVerify("packet verify failed");
        }

        // if ((option & CoderContent.COMPRESS_OPTION) > 0) {
        //     messageBytes = CompressUtils.decompressBytes(messageBytes);
        // }
        return this.messageCodec.decode(bodyBytes, as(tunnel.getMessageFactory()));
    }

}
