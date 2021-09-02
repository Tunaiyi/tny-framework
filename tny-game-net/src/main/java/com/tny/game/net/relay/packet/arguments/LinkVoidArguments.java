package com.tny.game.net.relay.packet.arguments;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/25 3:26 下午
 */
public class LinkVoidArguments implements LinkPacketArguments {

	private static final LinkVoidArguments VOID_ARGUMENTS = new LinkVoidArguments();

	public static LinkVoidArguments of() {
		return VOID_ARGUMENTS;
	}

	private LinkVoidArguments() {
	}

}
