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

package com.tny.game.net.netty4.relay.codec;

import com.tny.game.net.netty4.network.codec.*;
import com.tny.game.net.netty4.relay.codec.arguments.*;
import com.tny.game.net.relay.packet.*;
import com.tny.game.net.relay.packet.arguments.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;

import static com.tny.game.net.relay.RelayCodecConstants.*;
import static org.slf4j.LoggerFactory.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/6 8:46 下午
 */
public class RelayPacketV1Encoder implements RelayPacketEncoder, RelayPacketCodec {

    public static final Logger LOGGER = getLogger(RelayPacketV1Encoder.class);

    private final RelayPacketArgumentsCodecService relayPacketArgumentsCodecService;

    public RelayPacketV1Encoder(RelayPacketArgumentsCodecService relayPacketArgumentsCodecService) {
        this.relayPacketArgumentsCodecService = relayPacketArgumentsCodecService;
    }

    @Override
    public void encodeObject(ChannelHandlerContext ctx, RelayPacket<?> relay, ByteBuf out) {
        try {
            // 包头
            out.writeBytes(RELAY_MAGIC);
            RelayPacketType relayType = relay.getType();
            // 转发包字节开始的 index
            int lengthIndex = out.writerIndex();
            // 跳过长度 4 字节
            out.writerIndex(lengthIndex + RELAY_PACKET_ARGUMENTS_LENGTH_BYTES_SIZE);
            int packetBytesStartIndex = out.writerIndex();
            // 写入转发包id 8 字节
            NettyVarIntCoder.writeFixed32(relay.getId(), out);
            // 写入转发包Option
            byte option = relayType.getOption();
            out.writeByte(option);
            // 写入转发包发送时间
            NettyVarIntCoder.writeFixed64((relay.getTime()), out);
            // 写入转发包参数
            RelayPacketArguments arguments = relay.getArguments();
            if (arguments != null) {
                relayPacketArgumentsCodecService.encode(ctx, arguments, out);
            }
            out.markWriterIndex();
            // 计算写入长度
            int packetBytesLength = out.writerIndex() - packetBytesStartIndex;
            // 跳到 lengthIndex 写入长度
            out.writerIndex(lengthIndex);
            NettyVarIntCoder.writeFixed32(packetBytesLength, out);
            // 返回写入末尾
            out.resetWriterIndex();
        } catch (Exception e) {
            LOGGER.error("编码 relay packet {} 异常", relay, e);
        }
    }

}
