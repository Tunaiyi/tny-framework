package com.tny.game.net.relay.packet.arguments;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/10 11:51 上午
 */
public interface TunnelPacketArguments extends RelayPacketArguments {

	long getTunnelId();

	long getInstanceId();

}
