/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
            // 包头：必须堆缓冲——下方 verifier/crypto 以 array()/arrayOffset() 访问
            //（与 NetPacketV1Decoder.readPayload 的 heapBuffer 对称；direct buffer 上 array() 必炸）
            bodyBuffer = out.alloc().heapBuffer(counter.allot());
            this.messageCodec.encode(as(message), bodyBuffer);
            byte[] verifyCodeBytes = new byte[0];
            if (config.isVerifyEnable()) {
                // 生成校验码
                payloadLength += this.verifier.getCodeLength();
                verifyCodeBytes = this.verifier.generate(packager, bodyBuffer.array(), bodyBuffer.arrayOffset(), bodyBuffer.readableBytes());
            }
            if (logger.isDebugEnabled()) { // 热路径避免参数装配开销
                logger.debug("sendMessage : accessId {} | number {} | randCode {} | packLength {} | wasteBitSize {} | verify {}", accessId, number,
                        packager.getPacketCode(), payloadLength, wasteWriter.getWasteBitSize(), config.isVerifyEnable());
            }
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
                // 超限本地拒绝（net-protocol 规格）：写出会毒化对端连接（对端解码超限即断链），
                // 且发送方无感知——改为抛编码异常使本次写回执失败，通道保持健康（D2）
                throw new NetPacketEncodeException("encode rejected: payload {} > maxPayloadLength {}",
                        payloadLength, config.getMaxPayloadLength());
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
