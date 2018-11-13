package com.tny.game.net.netty4.codec;


import com.tny.game.common.unit.UnitLoader;
import com.tny.game.net.codec.cryptoloy.XOrCodecCrypto;
import com.tny.game.net.codec.v1.DataPacketV1Config;
import com.tny.game.net.codec.verifier.CRC64CodecVerifier;
import com.tny.game.net.message.*;
import com.tny.game.net.message.protoex.*;
import com.tny.game.net.netty4.*;
import com.tny.game.net.transport.*;
import io.netty.buffer.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.*;

import java.util.*;

import static com.tny.game.test.MockAide.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-11-01 14:10
 */
public class DataPacketV1CodecTest {

    private ChannelHandlerContext ctx;

    private ProtoExMessageFactory<Long> factory = new ProtoExMessageFactory<>();

    private DataPacketV1Decoder decoder;

    private DataPacketV1Encoder encoder;

    @BeforeClass
    public static void initUnit() {
        UnitLoader.register(new XOrCodecCrypto());
        UnitLoader.register(new ProtoExCodec<>());
        UnitLoader.register(new CRC64CodecVerifier());
    }

    private Message<Long> craeteMessage(Object... params) {
        return factory.create(1, MessageContexts.requestParams(
                ProtocolAide.protocol(100_199), params), Certificates.createAutherized(1, 1002L));
    }

    @Before
    public void setUp() {
        ctx = mockAs(ChannelHandlerContext.class);
        EmbeddedChannel channel = new EmbeddedChannel();
        when(ctx.channel()).thenReturn(channel);
        NettyTunnel tunnel = mockAs(NettyTunnel.class);
        when(tunnel.getAccessId()).thenReturn(2018L);
        channel.attr(NettyAttrKeys.TUNNEL).set(tunnel);
        DataPacketV1Config config = new DataPacketV1Config()
                .setSecurityKeys(new String[]{"1s1394d3kssvonxasanfkwhzfk0jy0zm"});
        decoder = new DataPacketV1Decoder(config);
        encoder = new DataPacketV1Encoder(config);
        decoder.prepareStart();
        encoder.prepareStart();
    }

    @Test
    public void codec() throws Exception {
        Message<Long> message = craeteMessage("1222", 300);
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(400);
        encoder.encodeObject(ctx, message, buffer);
        Message<?> readMessage = decoder.decodeObject(ctx, buffer);
        assertEquals(message.getHeader(), readMessage.getHeader());
        assertTrue(CollectionUtils.isEqualCollection(message.getBody(Collection.class), readMessage.getBody(Collection.class)));
    }
}