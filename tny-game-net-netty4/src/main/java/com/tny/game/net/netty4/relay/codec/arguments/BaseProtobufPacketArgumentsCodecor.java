package com.tny.game.net.netty4.relay.codec.arguments;

import com.baidu.bjf.remoting.protobuf.*;
import com.google.protobuf.*;
import com.tny.game.net.base.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.netty4.relay.codec.arguments.protobuf.*;
import com.tny.game.net.relay.packet.arguments.*;
import io.netty.buffer.*;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.*;

import java.io.IOException;
import java.util.function.Function;

import static com.google.protobuf.CodedOutputStream.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/9 8:01 下午
 */
public abstract class BaseProtobufPacketArgumentsCodecor<A extends RelayPacketArguments, P extends PacketArgumentsProto<A>> implements RelayPacketArgumentsCodecor<A> {

    public static final Logger LOGGER = LoggerFactory.getLogger(BaseProtobufPacketArgumentsCodecor.class);

    private final Class<A> argumentsClass;

    private final Function<A, P> protobufCreator;

    private final Codec<P> codec;

    public BaseProtobufPacketArgumentsCodecor(Class<A> argumentsClass, Class<P> protobufClass, Function<A, P> protobufCreator) {
        this.codec = ProtobufProxy.create(protobufClass);
        this.argumentsClass = argumentsClass;
        this.protobufCreator = protobufCreator;
    }

    @Override
    public Class<A> getArgumentsClass() {
        return argumentsClass;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, A arguments, ByteBuf out) {
        P proto = protobufCreator.apply(arguments);
        CodedOutputStream stream = newInstance(new ByteBufOutputStream(out));
        try {
            codec.writeTo(proto, stream);
            stream.flush();
        } catch (IOException e) {
            LOGGER.error("", e);
            throw new NetGeneralException(NetResultCode.ENCODE_FAILED);
        }
    }

    @Override
    public A decode(ChannelHandlerContext ctx, ByteBuf out) {
        CodedInputStream stream = CodedInputStream.newInstance(new ByteBufInputStream(out));
        try {
            P proto = codec.readFrom(stream);
            return proto.toArguments();
        } catch (IOException e) {
            LOGGER.error("", e);
            throw new NetGeneralException(NetResultCode.ENCODE_FAILED);
        }
    }

}
