package com.tny.game.net.relay.packet.arguments;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/10 12:10 下午
 */
public class TubuleConnectArguments implements RelayPacketArguments {

	private final int[] ipValue;

	private final String ip;

	private final int port;

	public TubuleConnectArguments(int[] ip, int port) {
		this.ipValue = ip;
		this.ip = ip[0] + "." + ip[1] + "." + ip[2] + "." + ip[3];
		this.port = port;
	}

	public String getIp() {
		return this.ip;
	}

	public int[] getIpValue() {
		return ipValue;
	}

	public int getPort() {
		return this.port;
	}

}
