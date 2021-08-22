package com.tny.game.relay.packet.arguments;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/10 12:05 下午
 */
public class VoidPacketArguments implements RelayPacketArguments {

	private static final VoidPacketArguments VOID_ARGUMENTS = new VoidPacketArguments();

	public static VoidPacketArguments of() {
		return VOID_ARGUMENTS;
	}

}
