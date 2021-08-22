package com.tny.game.relay.packet.arguments;

import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/10 12:10 下午
 */
public class TunnelConnectArguments implements RelayPacketArguments {

	private final int[] ipValue;

	private final String ip;

	private final int port;

	public TunnelConnectArguments(byte[] ip, int port) {
		this.ipValue = new int[4];
		for (int i = 0; i < ipValue.length; i++) {
			ipValue[i] = ip[i] & 0xff;
		}
		this.ip = StringUtils.join(ipValue, ',');
		this.port = port;
	}

	public TunnelConnectArguments(int[] ip, int port) {
		this.ipValue = ip;
		this.ip = StringUtils.join(ipValue, ',');
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
