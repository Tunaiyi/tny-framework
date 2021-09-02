package com.tny.game.net.netty4.relay.codec.arguments.protobuf;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/24 7:55 下午
 */
public interface PacketArgumentsProto<T> {

	T toArguments();

}
