package com.tny.game.net.netty4;

import com.tny.game.common.binary.*;
import com.tny.game.net.codec.*;
import com.tny.game.net.codec.v1.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.message.codec.*;
import com.tny.game.net.message.common.*;
import com.tny.game.net.netty4.codec.*;
import com.tny.game.net.transport.*;
import io.netty.buffer.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
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

    public static void main(String[] args) {
        DataPacketV1Config config = new DataPacketV1Config();
        MessageCodec codec = new DefaultMessageCodec(new MessageBodyCodec<String>() {

            @Override
            public String decode(ByteBuffer buffer) throws Exception {
                return new String(buffer.array(), UTF_8);
            }

            @Override
            public ByteBuffer encode(String object) {
                byte[] bytes = object.getBytes(UTF_8);
                System.out.println(BytesAide.toHexString(bytes));
                return ByteBuffer.wrap(bytes);
            }

        });
        DataPacketV1Encoder packetV1Encoder = new DataPacketV1Encoder(config);
        packetV1Encoder.setMessageCodec(codec);

        CommonMessageFactory messageFactory = new CommonMessageFactory();

        ChannelHandlerContext ctx = mockAs(ChannelHandlerContext.class);

        NetTunnel<?> tunnel = mockAs(NetTunnel.class);
        when(tunnel.getAccessId()).thenReturn(999999L);
        when(tunnel.getMessageFactory()).thenReturn(messageFactory);

        EmbeddedChannel channel = new EmbeddedChannel();
        channel.attr(NettyAttrKeys.TUNNEL).set(tunnel);
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
                    message = messageFactory.create(index, MessageContexts.push(Protocols.protocol(protocolId), body));
                    break;
                case 1:
                    message = messageFactory.create(index, MessageContexts.respond(Protocols.protocol(protocolId), body, index));
                    break;
                case 2:
                    message = messageFactory.create(index, MessageContexts.request(Protocols.protocol(protocolId), body));
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

        DataPacketV1Decoder packetV1Decoder = new DataPacketV1Decoder(config);
        packetV1Decoder.setMessageCodec(codec);
        DecoderHandler decoder = new DecoderHandler(packetV1Decoder);
        byteBuf = allocator.buffer();
        try (
                RandomAccessFile file = new RandomAccessFile("/Users/kgtny/Desktop/cshap.bin", "rw");
                FileChannel fileChannel = file.getChannel()
        ) {
            byteBuf.writeBytes(fileChannel, 0, (int)fileChannel.size());
            List<Object> messags = new ArrayList<>();
            decoder.decode(ctx, byteBuf, messags);
            for (Object data : messags) {
                Message message = as(data);
                System.out.println(format("ID : {} Mode : {} Body : {}",
                        message.getId(), message.getMode(), message.getBody(String.class)));
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
