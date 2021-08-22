package com.tny.game.relay.packet.arguments;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/10 12:05 下午
 */
public class TunnelConnectedArguments implements RelayPacketArguments {

	private static final TunnelConnectedArguments SUCCESS = new TunnelConnectedArguments(true);

	private static final TunnelConnectedArguments FAILURE = new TunnelConnectedArguments(false);

	private boolean result;

	public TunnelConnectedArguments() {
	}

	private TunnelConnectedArguments(boolean result) {
		this.result = result;
	}

	public boolean getResult() {
		return result;
	}

	public boolean isSuccess() {
		return result;
	}

	public boolean isFailure() {
		return !result;
	}

	public static TunnelConnectedArguments success() {
		return SUCCESS;
	}

	public static TunnelConnectedArguments failure() {
		return FAILURE;
	}

	public static TunnelConnectedArguments of(boolean result) {
		return result ? SUCCESS : FAILURE;
	}

}
