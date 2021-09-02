package com.tny.game.net.relay.packet.arguments;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/10 12:05 下午
 */
public class TunnelConnectedArguments extends BaseTunnelPacketArguments {

	private final boolean result;

	public static TunnelConnectedArguments success(long tunnelId) {
		return new TunnelConnectedArguments(tunnelId, true);
	}

	public static TunnelConnectedArguments failure(long tunnelId) {
		return new TunnelConnectedArguments(tunnelId, false);
	}

	public static TunnelConnectedArguments ofResult(long tunnelId, boolean result) {
		return new TunnelConnectedArguments(tunnelId, result);
	}

	private TunnelConnectedArguments(long tunnelId, boolean result) {
		super(tunnelId);
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

}
