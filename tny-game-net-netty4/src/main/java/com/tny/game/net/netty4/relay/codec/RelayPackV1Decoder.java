package com.tny.game.net.netty4.relay.codec;

import com.tny.game.common.enums.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.netty4.network.codec.*;
import com.tny.game.net.netty4.relay.codec.arguments.*;
import com.tny.game.net.relay.*;
import com.tny.game.net.relay.packet.*;
import com.tny.game.net.relay.packet.arguments.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.FastThreadLocal;
import org.slf4j.Logger;

import static com.tny.game.net.relay.RelayCodecConstants.*;
import static org.slf4j.LoggerFactory.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/6 8:46 下午
 */
public class RelayPackV1Decoder implements RelayPackDecoder, RelayPackCodec {

    public static final Logger LOGGER = getLogger(RelayPackV1Decoder.class);

    private static final FastThreadLocal<byte[]> MAGICS_LOCAL = new FastThreadLocal<byte[]>() {

        @Override
        protected byte[] initialValue() {
            return new byte[RelayCodecConstants.RELAY_MAGIC.length];
        }
    };

    private final RelayPacketArgumentsCodecService relayPacketArgumentsCodecService;

    public RelayPackV1Decoder(RelayPacketArgumentsCodecService relayPacketArgumentsCodecService) {
        this.relayPacketArgumentsCodecService = relayPacketArgumentsCodecService;
    }

    @Override
    public Object decodeObject(ChannelHandlerContext ctx, ByteBuf in) {
        in.markReaderIndex();
        if (in.readableBytes() < RELAY_PACKET_HEAD_LENGTH_BYTES_SIZE) {
            return null;
        }
        final byte[] magics = MAGICS_LOCAL.get();
        in.readBytes(magics);
        if (!isMagic(magics)) {
            throw NetCodecException.causeDecodeError("illegal relay magics");
        }
        int packetLength = NettyVarIntCoder.readFixed32(in);
        if (in.readableBytes() < packetLength) {
            in.resetReaderIndex();
            return null;
        }
        if (packetLength != 0) {
            ByteBuf packetBody = null;
            Class<?> argumentsClass = null;
            try {
                RelayPacketArguments arguments;
                packetBody = in.readBytes(packetLength);
                // 读取转发包id 8 字节
                int id = NettyVarIntCoder.readFixed32(packetBody);
                // 读取转发包Option
                byte option = packetBody.readByte();
                RelayPacketType relayType = EnumAide.check(RelayPacketType.class, (byte)(option & RELAY_PACKET_TYPE_MASK));
                // 读取转发包发送时间
                long time = NettyVarIntCoder.readFixed64(packetBody);
                argumentsClass = relayType.getArgumentsClass();
                arguments = relayPacketArgumentsCodecService.decode(ctx, packetBody, relayType);
                return relayType.createPacket(id, arguments, time);
            } catch (Exception e) {
                throw NetCodecException.causeDecodeError(e, "decode {} exception", argumentsClass);
            } finally {
                ReferenceCountUtil.release(packetBody);
            }
        } else {
            throw NetCodecException.causeDecodeError("packetLength is null");
        }
    }

}
