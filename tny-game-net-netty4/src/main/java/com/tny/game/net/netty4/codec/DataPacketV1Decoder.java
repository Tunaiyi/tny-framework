package com.tny.game.net.netty4.codec;

import com.tny.game.net.codec.*;
import com.tny.game.net.exception.CodecException;
import com.tny.game.net.message.*;
import com.tny.game.net.message.coder.CodecContent;
import com.tny.game.net.netty4.NettyAttrKeys;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import org.slf4j.*;

import static com.tny.game.net.message.coder.CodecContent.*;

public class DataPacketV1Decoder implements DataPacketDecoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataPacketV1Decoder.class);

    private CodecConfig config;

    public DataPacketV1Decoder(CodecConfig config) {
        super();
        this.config = config;
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

        if (isOption(option, PING_OPTION)) {
            return DetectMessage.ping();
        }

        if (isOption(option, PONG_OPTION)) {
            return DetectMessage.pong();
        }

        int payloadLength = in.readInt();
        // 读取请求信息体
        if (in.readableBytes() < payloadLength) {
            in.resetReaderIndex();
            return null;
        }
        // 获取打包器
        long accessId = NettyVarintCoder.readVarint64(in);
        DataPackager packager = channel.attr(NettyAttrKeys.READ_PACKAGER).get();
        if (packager == null) {
            packager = new DataPackager(accessId, config.getPacketConfig());
            channel.attr(NettyAttrKeys.READ_PACKAGER).set(packager);
        }

        final int payloadSize = in.readInt();
        // 检测 payload 长度
        if (in.readableBytes() < payloadSize) {
            in.resetReaderIndex();
            return null;
        }
        return readPayload(packager, in, option, payloadSize);
    }


    private Message<?> readPayload(DataPackager packager, ByteBuf in, byte option, int payloadSize) throws Exception {

        int index = in.readerIndex();
        int number = NettyVarintCoder.readVarint32(in);
        int time = NettyVarintCoder.readVarint32(in);

        DataPacketConfig packetConfig = config.getPacketConfig();

        // 移动到当前包序号
        packager.goToAndCheck(number);

        boolean verifyEnable = isOption(option, OPTION_VERIFY);
        if (packetConfig.isVerifyOpen() && !verifyEnable)
            throw CodecException.causeDecode("packet need verify!");
        boolean encryptEnable = isOption(option, OPTION_ENCRYPT);
        if (packetConfig.isEncryptOpen() && !encryptEnable)
            throw CodecException.causeDecode("packet need encrypt!");
        boolean wasteBytesEnable = isOption(option, OPTION_WASTE_BYTES);
        if (packetConfig.isWasteBytesOpen() && !wasteBytesEnable)
            throw CodecException.causeDecode("packet need waste bytes!");

        // 检测时间
        packager.checkPacketTime(time);

        //计算 body length
        NettyWasteReader reader = new NettyWasteReader(packager, wasteBytesEnable, packetConfig);
        int verifyLength = verifyEnable ? config.getVerifier().getCodeLength() : 0;
        int bodyLength = payloadSize - verifyLength - (in.readerIndex() - index);
        // 读取废字节中的 bodyBytes
        byte[] bodyBytes = reader.read(bodyLength);

        // 加密
        if (encryptEnable) {
            CodecCryptology cryptology = config.getCryptology();
            bodyBytes = cryptology.decrypt(packager, bodyBytes);
            CodecLogger.logBinary(LOGGER, "sendMessage body decryption |  body  {} ", bodyBytes);
        }

        // 加密
        if (verifyEnable) {
            CodecVerifier verifier = config.getVerifier();
            byte[] verifyCode = new byte[verifyLength];
            in.readBytes(verifyCode);
            if (verifier.verify(packager, bodyBytes, time, verifyCode))
                throw CodecException.causeVerify("packet verify failed");
        }

        // if ((option & CoderContent.COMPRESS_OPTION) > 0) {
        //     messageBytes = CompressUtils.decompressBytes(messageBytes);
        // }
        return this.config.getMessageCodec().decode(bodyBytes);
    }

}
