package com.tny.game.net.relay.packet.arguments;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/25 3:26 下午
 */
public class LinkOpenedArguments implements LinkPacketArguments {

	public static LinkOpenedArguments success() {
		return new LinkOpenedArguments(true);
	}

	public static LinkOpenedArguments failure() {
		return new LinkOpenedArguments(false);
	}

	public static LinkOpenedArguments of(boolean result) {
		return new LinkOpenedArguments(result);
	}

	private final boolean result;

	private LinkOpenedArguments(boolean success) {
		this.result = success;
	}

	public boolean getResult() {
		return result;
	}

	public boolean isResult() {
		return result;
	}

	public boolean isFailure() {
		return !result;
	}

	public boolean isSuccess() {
		return result;
	}

}
