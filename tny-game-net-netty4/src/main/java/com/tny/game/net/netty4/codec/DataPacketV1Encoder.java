package com.tny.game.net.netty4.codec;

import com.tny.game.net.codec.*;
import com.tny.game.net.codec.v1.DataPacketV1Config;
import com.tny.game.net.message.*;
import com.tny.game.net.message.coder.CodecContent;
import com.tny.game.net.netty4.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import org.slf4j.*;

import static com.tny.game.net.message.coder.CodecContent.*;

public class DataPacketV1Encoder extends DataPacketV1BaseCodec implements DataPacketEncoder {

    public static final Logger LOGGER = LoggerFactory.getLogger(DataPacketV1Encoder.class);

    public DataPacketV1Encoder() {
        super();
    }

    public DataPacketV1Encoder(DataPacketV1Config config) {
        super(config);
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
                NettyTunnel<?> tunnel = channel.attr(NettyAttrKeys.TUNNEL).get();
                packager = new DataPackager(tunnel.getAccessId(), config);
                channel.attr(NettyAttrKeys.WRITE_PACKAGER).set(packager);
            }
            writePayload(packager, message, out);
        } catch (Exception e) {
            LOGGER.error("编码 message {} 异常", message, e);
        }
    }

    private void writePayload(DataPackager packager, Message<?> message, ByteBuf out) throws Exception {

        // 写入 Option
        byte option = 0;
        option = CodecContent.setOption(option, OPTION_VERIFY, config.isVerifyEnable());
        option = CodecContent.setOption(option, OPTION_ENCRYPT, config.isEncryptEnable());
        option = CodecContent.setOption(option, OPTION_WASTE_BYTES, config.isWasteBytesEnable());
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
        NettyWasteWriter wasteWriter = new NettyWasteWriter(packager, config);
        payloadLength += wasteWriter.getTotalWasteByteSize();

        // 包头 包体
        byte[] body = this.codec.encode(message);

        byte[] verifyCodeBytes = new byte[0];
        if (config.isVerifyEnable()) {
            // 生成校验码
            payloadLength += verifier.getCodeLength();
            verifyCodeBytes = verifier.generate(packager, body, time);
        }

        LOGGER.debug("sendMessage : accessId {} | number {} | randCode {} | packLength {} | wasteBitSize {} | verify {}", accessId, number, packager.getPacketCode(), payloadLength, wasteWriter.getWasteBitSize(), config.isVerifyEnable());

        // 加密
        if (config.isEncryptEnable()) {
            body = crypto.encrypt(packager, body);
            CodecLogger.logBinary(LOGGER, "sendMessage body decryption |  body  {} ", body);
        }

        // 包体长度
        // payloadLength += NettyVarintCoder.varint32Size(body.length);
        payloadLength += body.length;

        // 写入包长度
        out.writeInt(payloadLength);

        // 写入 accessId
        NettyVarintCoder.writeVarint64(accessId, out);
        // 写入 number
        NettyVarintCoder.writeVarint32(number, out);
        // 写入 时间
        NettyVarintCoder.writeVarint64(time, out);

        LOGGER.debug("out payloadIndex start {}", out.writerIndex());
        // 写入包体长度 包体
        wasteWriter.write(out, body);
        LOGGER.debug("out payloadIndex end {}", out.writerIndex());

        // 校验码
        if (verifyCodeBytes != null)
            out.writeBytes(verifyCodeBytes);
    }


}
