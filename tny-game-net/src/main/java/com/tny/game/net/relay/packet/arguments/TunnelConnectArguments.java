package com.tny.game.net.relay.packet.arguments;

import com.tny.game.net.relay.link.*;
import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/10 12:10 下午
 */
public class TunnelConnectArguments extends BaseTunnelPacketArguments {

	private final int[] ipValue;

	private final String ip;

	private final int port;

	public TunnelConnectArguments(RelayTunnel<?> tunnel, byte[] ip, int port) {
		this(tunnel.getInstanceId(), tunnel.getId(), ip, port);
	}

	public TunnelConnectArguments(long instanceId, long tunnelId, byte[] ip, int port) {
		super(instanceId, tunnelId);
		this.ipValue = new int[4];
		for (int i = 0; i < ipValue.length; i++) {
			ipValue[i] = ip[i] & 0xff;
		}
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
