package com.tny.game.net.message.codec;

import com.tny.game.common.lifecycle.unit.*;
import com.tny.game.net.codec.cryptoloy.*;
import com.tny.game.net.codec.verifier.*;
import com.tny.game.net.message.*;
import com.tny.game.net.message.common.*;
import com.tny.game.net.netty4.network.*;
import com.tny.game.net.netty4.network.codec.*;
import com.tny.game.net.transport.*;
import io.netty.buffer.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;

import java.util.Collection;

import static com.tny.game.test.MockAide.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-11-01 14:10
 */
public class DataPacketV1CodecTest {

    private ChannelHandlerContext ctx;

    private CommonMessageFactory factory = new CommonMessageFactory();

    private DatagramPackV1Decoder decoder;

    private DatagramPackV1Encoder encoder;

    @BeforeAll
    public static void initUnit() {
        UnitLoader.register(new XOrCodecCrypto());
        UnitLoader.register(new ProtoExMessageBodyCodec<>());
        UnitLoader.register(new CRC64CodecVerifier());
    }

    private Message craeteMessage(Object... params) {
        return this.factory.create(1, MessageContexts.request(
                Protocols.protocol(100_199), params));
    }

    @Before
    public void setUp() {
        this.ctx = mockAs(ChannelHandlerContext.class);
        EmbeddedChannel channel = new EmbeddedChannel();
        when(this.ctx.channel()).thenReturn(channel);
        NetTunnel tunnel = mockAs(NetTunnel.class);
        when(tunnel.getAccessId()).thenReturn(2018L);
        when(tunnel.getMessageFactory()).thenReturn(this.factory);
        channel.attr(NettyNetAttrKeys.TUNNEL).set(tunnel);
        DatagramPackCodecSetting config = new DatagramPackCodecSetting();
        config.setSecurityKeys(new String[]{"1s1394d3kssvonxasanfkwhzfk0jy0zm"});
        this.decoder = new DatagramPackV1Decoder(config);
        this.encoder = new DatagramPackV1Encoder(config);
        this.decoder.prepareStart();
        this.encoder.prepareStart();
    }

    @Test
    public void codec() throws Exception {
        Message message = craeteMessage("1222", 300);
        DatagramPackDecodeMarker marker = new DatagramPackDecodeMarker();
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(400);
        this.encoder.encodeObject(this.ctx, message, buffer);
        Message readMessage = this.decoder.decodeObject(this.ctx, buffer, marker);
        assertEquals(message.getHead(), readMessage.getHead());
        assertTrue(CollectionUtils.isEqualCollection(message.bodyAs(Collection.class), readMessage.bodyAs(Collection.class)));
    }

}