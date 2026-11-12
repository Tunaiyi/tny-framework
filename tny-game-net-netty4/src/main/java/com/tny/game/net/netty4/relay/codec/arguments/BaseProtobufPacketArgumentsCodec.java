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
package com.tny.game.net.netty4.relay.codec.arguments;

import com.baidu.bjf.remoting.protobuf.*;
import com.google.protobuf.*;
import com.tny.game.net.application.*;
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
public abstract class BaseProtobufPacketArgumentsCodec<A extends RelayPacketArguments, P extends PacketArgumentsProto<A>>
        implements RelayPacketArgumentsCodec<A> {

    public static final Logger LOGGER = LoggerFactory.getLogger(BaseProtobufPacketArgumentsCodec.class);

    private final Class<A> classOfArguments;

    private final Function<A, P> protobufCreator;

    private final Codec<P> codec;

    public BaseProtobufPacketArgumentsCodec(Class<A> classOfArguments, Class<P> protobufClass, Function<A, P> protobufCreator) {
        this.codec = ProtobufProxy.create(protobufClass);
        this.classOfArguments = classOfArguments;
        this.protobufCreator = protobufCreator;
    }

    @Override
    public Class<A> getClassOfArguments() {
        return classOfArguments;
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
            throw new NetException(NetResultCode.ENCODE_FAILED);
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
            throw new NetException(NetResultCode.ENCODE_FAILED);
        }
    }

}
