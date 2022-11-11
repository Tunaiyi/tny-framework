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

public class NetPacketV1Decoder extends NetPacketV1Codec implements NetPacketDecoder {

    public NetPacketV1Decoder(NetPacketCodecSetting config) {
        super(config);
    }

    public NetPacketV1Decoder() {
    }

    private static final FastThreadLocal<byte[]> MAGICS_LOCAL = new FastThreadLocal<byte[]>() {

        @Override
        protected byte[] initialValue() {
            return new byte[CodecConstants.FRAME_MAGIC.length];
        }
    };

    @Override
    public Message decodeObject(ChannelHandlerContext ctx, ByteBuf in, NetPacketDecodeMarker marker) throws Exception {
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
                throw NetCodecException.causeDecodeError("illegal magics");
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
                throw NetCodecException.causeDecodeError("decode message failed, because payloadLength {} > maxPayloadLength {}",
                        payloadLength, config.getMaxPayloadLength());
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
            boolean verifyEnable = isOption(option, DATA_PACK_OPTION_VERIFY);
            if (config.isVerifyEnable() && !verifyEnable) {
                throw NetCodecException.causeDecodeError("packet need verify!");
            }
            boolean encryptEnable = isOption(option, DATA_PACK_OPTION_ENCRYPT);
            if (config.isEncryptEnable() && !encryptEnable) {
                throw NetCodecException.causeDecodeError("packet need encrypt!");
            }
            boolean wasteBytesEnable = isOption(option, DATA_PACK_OPTION_WASTE_BYTES);
            if (config.isWasteBytesEnable() && !wasteBytesEnable) {
                throw NetCodecException.causeDecodeError("packet need waste bytes!");
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
                    throw NetCodecException.causeVerify("packet verify failed");
                }
            }
            return this.messageCodec.decode(bodyBuffer, as(tunnel.getMessageFactory()));
        } finally {
            ReferenceCountUtil.release(bodyBuffer);
        }
    }

}
