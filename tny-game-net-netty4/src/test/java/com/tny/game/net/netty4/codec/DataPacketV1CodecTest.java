package com.tny.game.net.netty4.codec;

import com.tny.game.common.unit.*;
import com.tny.game.net.codec.cryptoloy.*;
import com.tny.game.net.codec.v1.*;
import com.tny.game.net.codec.verifier.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.message.common.*;
import com.tny.game.net.message.protoex.*;
import com.tny.game.net.netty4.*;
import io.netty.buffer.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.*;

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

    private CommonMessageFactory<Long> factory = new CommonMessageFactory<>();

    private DataPacketV1Decoder decoder;

    private DataPacketV1Encoder encoder;

    @BeforeClass
    public static void initUnit() {
        UnitLoader.register(new XOrCodecCrypto());
        UnitLoader.register(new ProtoExCodec<>());
        UnitLoader.register(new CRC64CodecVerifier());
    }

    private Message craeteMessage(Object... params) {
        return this.factory.create(1, MessageContexts.requestParams(
                ProtocolAide.protocol(100_199), params));
    }

    @Before
    public void setUp() {
        this.ctx = mockAs(ChannelHandlerContext.class);
        EmbeddedChannel channel = new EmbeddedChannel();
        when(this.ctx.channel()).thenReturn(channel);
        NettyTunnel tunnel = mockAs(NettyTunnel.class);
        when(tunnel.getAccessId()).thenReturn(2018L);
        when(tunnel.getMessageFactory()).thenReturn(this.factory);
        channel.attr(NettyAttrKeys.TUNNEL).set(tunnel);
        DataPacketV1Config config = new DataPacketV1Config()
                .setSecurityKeys(new String[]{"1s1394d3kssvonxasanfkwhzfk0jy0zm"});
        this.decoder = new DataPacketV1Decoder(config);
        this.encoder = new DataPacketV1Encoder(config);
        this.decoder.prepareStart();
        this.encoder.prepareStart();
    }

    @Test
    public void codec() throws Exception {
        Message message = craeteMessage("1222", 300);
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(400);
        this.encoder.encodeObject(this.ctx, message, buffer);
        Message readMessage = this.decoder.decodeObject(this.ctx, buffer);
        assertEquals(message.getHead(), readMessage.getHead());
        assertTrue(CollectionUtils.isEqualCollection(message.getBody(Collection.class), readMessage.getBody(Collection.class)));
    }

}