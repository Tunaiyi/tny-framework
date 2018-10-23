package com.tny.game.net.netty4.codec;

import com.tny.game.net.codec.*;
import com.tny.game.net.message.*;
import com.tny.game.net.netty4.NettyAttrKeys;
import com.tny.game.net.message.coder.CodecContent;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import org.slf4j.*;

import static com.tny.game.common.utils.StringAide.*;
import static com.tny.game.net.message.coder.CodecContent.*;

public class DataPacketV1Encoder implements DataPacketEncoder {

    public static final Logger LOGGER = LoggerFactory.getLogger(DataPacketV1Encoder.class);

    private CodecConfig config;

    public DataPacketV1Encoder(CodecConfig config) {
        super();
        this.config = config;
    }

    @Override
    public void encodeObject(ChannelHandlerContext ctx, Message<?> message, ByteBuf out) {
        Channel channel = ctx.channel();
        try {
            out.writeBytes(FRAME_MAGIC);
            if (message.getMode() == MessageMode.PING) {
                out.writeByte(CodecContent.PING_OPTION);
                out.writeInt(0); // BodySize
                return;
            }
            if (message.getMode() == MessageMode.PONG) {
                out.writeByte(CodecContent.PONG_OPTION);
                out.writeInt(0); // BodySize
                return;
            }
            // 获取打包器
            DataPackager packager = channel.attr(NettyAttrKeys.WRITE_PACKAGER).get();
            if (packager == null) {
                Long accessId = channel.attr(NettyAttrKeys.ACCESS_ID).get();
                if (accessId == null)
                    throw new NullPointerException(format("{} no accessId"));
                packager = new DataPackager(accessId, config.getPacketConfig());
                channel.attr(NettyAttrKeys.WRITE_PACKAGER).set(packager);
            }
            writePayload(packager, message, out);
        } catch (Exception e) {
            LOGGER.error("编码 message {} 异常", message, e);
        }
    }

    private void writePayload(DataPackager packager, Message<?> message, ByteBuf out) throws Exception {

        // 写入 包头
        out.writeBytes(FRAME_MAGIC);

        DataPacketConfig packetConfig = config.getPacketConfig();

        // 写入 Option
        byte option = 0;
        option = CodecContent.setOption(option, OPTION_VERIFY, packetConfig.isVerifyOpen());
        option = CodecContent.setOption(option, OPTION_ENCRYPT, packetConfig.isEncryptOpen());
        option = CodecContent.setOption(option, OPTION_WASTE_BYTES, packetConfig.isWasteBytesOpen());
        out.writeByte(option);

        int payloadLength = 0;

        // accessId
        long accessId = packager.getAccessId();
        payloadLength += NettyVarintCoder.varint64Size(accessId);
        // number
        int number = packager.nextNumber();
        payloadLength += NettyVarintCoder.varint32Size(number);
        // 时间
        long time = System.currentTimeMillis();
        payloadLength += NettyVarintCoder.varint64Size(time);
        // 计算废字节
        NettyWasteWriter wasteWriter = new NettyWasteWriter(packager, packetConfig);
        payloadLength += wasteWriter.getTotalWasteByteSize();

        // 包头 包体
        byte[] body = this.config.getMessageCodec().encode(message);

        byte[] verifyCodeBytes = new byte[0];
        if (packetConfig.isVerifyOpen()) {
            // 生成校验码
            com.tny.game.net.codec.CodecVerifier verifier = config.getVerifier();
            payloadLength += verifier.getCodeLength();
            verifyCodeBytes = verifier.generate(packager, body, time);
        }

        LOGGER.debug("sendMessage : accessId {} | number {} | randCode {} | packLength {} | wasteBitSize {} | verify {}", accessId, number, packager.getPacketCode(), payloadLength, wasteWriter.getWasteBitSize(), packetConfig.isVerifyOpen());

        // 加密
        if (packetConfig.isEncryptOpen()) {
            CodecCryptology cryptology = config.getCryptology();
            body = cryptology.encrypt(packager, body);
            CodecLogger.logBinary(LOGGER, "sendMessage body decryption |  body  {} ", body);
        }

        // 包体长度
        payloadLength += NettyVarintCoder.varint32Size(body.length);
        payloadLength += body.length;

        // 写入包长度
        out.writeInt(payloadLength);

        // 写入 accessId
        NettyVarintCoder.writeVarint64(accessId, out);
        // 写入 number
        NettyVarintCoder.writeVarint32(number, out);
        // 写入 时间
        NettyVarintCoder.writeVarint64(time, out);

        // 写入包体长度 包体
        wasteWriter.wasteBuffer(out);
        wasteWriter.write(body);

        // 校验码
        if (verifyCodeBytes != null)
            out.writeBytes(verifyCodeBytes);
    }


    private class CodecVerifier {
    }
}
