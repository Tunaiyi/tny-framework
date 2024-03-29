/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.net.netty4.network.codec;

import com.tny.game.net.codec.*;
import com.tny.game.net.message.*;
import com.tny.game.net.netty4.network.*;
import com.tny.game.net.transport.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.FastThreadLocal;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.net.message.CodecConstants.*;

public class NetPacketV1Encoder extends NetPacketV1Codec implements NetPacketEncoder {

    private final FastThreadLocal<MemoryAllotCounter> counterThreadLocal = new FastThreadLocal<>();

    public NetPacketV1Encoder() {
        super();
    }

    public NetPacketV1Encoder(NetPacketCodecSetting config) {
        super(config);
    }

    @Override
    public void encodeObject(ChannelHandlerContext ctx, Message message, ByteBuf out) throws Exception {
        Channel channel = ctx.channel();
        out.writeBytes(FRAME_MAGIC);
        MessageType messageType = message.getMode().getType();
        if (messageType != MessageType.MESSAGE) {
            out.writeByte(messageType.getOption());
            return;
        }
        // 获取打包器
        DataPackageContext packageContext = channel.attr(NettyNetAttrKeys.WRITE_PACKAGER).get();
        NetTunnel tunnel;
        if (packageContext == null) {
            tunnel = channel.attr(NettyNetAttrKeys.TUNNEL).get();
            packageContext = new DataPackageContext(tunnel.getAccessId(), config);
            channel.attr(NettyNetAttrKeys.WRITE_PACKAGER).set(packageContext);
        }
        writePayload(packageContext, message, out);
    }

    private MemoryAllotCounter counter() {
        MemoryAllotCounter counter = counterThreadLocal.get();
        if (counter == null) {
            counterThreadLocal.set(counter = new MemoryAllotCounter());
        }
        return counter;
    }

    private void writePayload(DataPackageContext packager, Message message, ByteBuf out) throws Exception {
        ByteBuf bodyBuffer = null;
        MemoryAllotCounter counter = this.counter();
        int actualSize = 0;
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
            bodyBuffer = out.alloc().buffer(counter.allot());
            this.messageCodec.encode(as(message), bodyBuffer);
            byte[] verifyCodeBytes = new byte[0];
            if (config.isVerifyEnable()) {
                // 生成校验码
                payloadLength += this.verifier.getCodeLength();
                verifyCodeBytes = this.verifier.generate(packager, bodyBuffer.array(), bodyBuffer.arrayOffset(), bodyBuffer.readableBytes());
            }
            logger.debug("sendMessage : accessId {} | number {} | randCode {} | packLength {} | wasteBitSize {} | verify {}", accessId, number,
                    packager.getPacketCode(), payloadLength, wasteWriter.getWasteBitSize(), config.isVerifyEnable());
            // 加密
            if (config.isEncryptEnable()) {
                // TODO 是否需要重新创建 buffer
                this.crypto.encrypt(packager, bodyBuffer.array(), bodyBuffer.arrayOffset(), bodyBuffer.readableBytes());
                if (logger.isDebugEnabled()) {
                    CodecLogger.logBinary(logger, "sendMessage body decryption |  body  {} ", bodyBuffer.array(), bodyBuffer.arrayOffset(),
                            bodyBuffer.readableBytes());
                }
            }
            // 包体长度
            // payloadLength += NettyVarintCoder.varint32Size(body.length);
            payloadLength += bodyBuffer.readableBytes();
            if (payloadLength > config.getMaxPayloadLength()) {
                logger.warn("encode message {} failed payloadLength {} > maxPayloadLength {}", message, payloadLength, PAYLOAD_BYTES_MAX_SIZE);
            }
            // 写入包长度
            //        out.writeInt(payloadLength);
            NettyVarIntCoder.writeFixed32(payloadLength, out);
            // 写入 accessId
            NettyVarIntCoder.writeVarInt64(accessId, out);
            // 写入 number
            NettyVarIntCoder.writeVarInt32(number, out);
            logger.debug("out payloadIndex start {}", out.writerIndex());

            actualSize = bodyBuffer.readableBytes();
            // 写入包体长度 包体
            wasteWriter.write(out, bodyBuffer);
            logger.debug("out payloadIndex end {}", out.writerIndex());
            // 校验码
            if (verifyCodeBytes != null) {
                out.writeBytes(verifyCodeBytes);
            }
        } finally {
            counter.recode(actualSize);
            ReferenceCountUtil.release(bodyBuffer);
        }
    }

}
