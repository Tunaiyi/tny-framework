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

import com.tny.game.common.digest.binary.*;
import com.tny.game.net.message.*;
import com.tny.game.net.message.common.*;
import com.tny.game.net.netty4.network.*;
import com.tny.game.net.transport.*;
import io.netty.buffer.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;

import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;
import static com.tny.game.test.MockAide.*;
import static java.nio.charset.StandardCharsets.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/7 7:53 下午
 */
public class CoderTest {

    public static void main(String[] args) throws Exception {
        NetPacketCodecSetting config = new NetPacketCodecSetting();
        NettyMessageCodec codec = new DefaultNettyMessageCodec(new MessageBodyCodec<String>() {

            @Override
            public String decode(ByteBuf buffer) throws Exception {
                return new String(buffer.array(), UTF_8);
            }

            @Override
            public void encode(String object, ByteBuf code) throws Exception {
                byte[] bytes = object.getBytes(UTF_8);
                System.out.println(BytesAide.toHexString(bytes));
                code.writeBytes(bytes);
            }

        }, new DefaultMessageHeaderCodec());
        NetPacketV1Encoder packetV1Encoder = new NetPacketV1Encoder(config);
        packetV1Encoder.setMessageCodec(codec);
        CommonMessageFactory messageFactory = new CommonMessageFactory();
        ChannelHandlerContext ctx = mockAs(ChannelHandlerContext.class);
        NetTunnel tunnel = mockAs(NetTunnel.class);
        when(tunnel.getAccessId()).thenReturn(999999L);
        when(tunnel.getMessageFactory()).thenReturn(messageFactory);
        EmbeddedChannel channel = new EmbeddedChannel();
        channel.attr(NettyNetAttrKeys.TUNNEL).set(tunnel);
        when(ctx.channel()).thenReturn(channel);
        UnpooledByteBufAllocator allocator = new UnpooledByteBufAllocator(false);
        ByteBuf byteBuf = allocator.buffer();
        for (int index = 0; index < 10; index++) {
            Message message = null;
            int protocolId = 1000 + index;
            int mode = index % 5;
            String body = format("This is protocol {} message index {}",
                    protocolId, index);
            switch (mode) {
                case 0:
                    message = messageFactory.create(index, MessageContents.push(Protocols.protocol(protocolId), body));
                    break;
                case 1:
                    message = messageFactory.create(index, MessageContents.respond(Protocols.protocol(protocolId), body, index));
                    break;
                case 2:
                    message = messageFactory.create(index, MessageContents.request(Protocols.protocol(protocolId), body));
                    break;
                case 3:
                    message = TickMessage.ping();
                    break;
                case 4:
                    message = TickMessage.pong();
                    break;
            }
            packetV1Encoder.encodeObject(ctx, message, byteBuf);
        }
        try (
                RandomAccessFile file = new RandomAccessFile("./net.bin", "rw");
                FileChannel fileChannel = file.getChannel()
        ) {
            byteBuf.readBytes(fileChannel, byteBuf.readableBytes());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        NetPacketV1Decoder packetV1Decoder = new NetPacketV1Decoder(config);
        packetV1Decoder.setMessageCodec(codec);
        NetPacketDecodeHandler decoder = new NetPacketDecodeHandler(packetV1Decoder, true);
        byteBuf = allocator.buffer();
        try (
                RandomAccessFile file = new RandomAccessFile("/Users/kgtny/Desktop/cshap.bin", "rw");
                FileChannel fileChannel = file.getChannel()
        ) {
            byteBuf.writeBytes(fileChannel, 0, (int) fileChannel.size());
            List<Object> messags = new ArrayList<>();
            decoder.decode(ctx, byteBuf, messags);
            for (Object data : messags) {
                Message message = as(data);
                System.out.println(format("ID : {} Mode : {} Body : {}",
                        message.getId(), message.getMode(), message.bodyAs(String.class)));
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
