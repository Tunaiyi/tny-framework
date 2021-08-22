package com.tny.game.relay.transport;

import com.tny.game.net.base.*;
import com.tny.game.net.transport.*;
import com.tny.game.relay.exception.*;
import com.tny.game.relay.packet.*;
import com.tny.game.relay.packet.arguments.*;
import org.slf4j.*;

import java.net.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/20 4:59 下午
 */
public class CommonRelayLinkExplorer<UID> implements RelayLinkExplorer<UID> {

	public static final Logger LOGGER = LoggerFactory.getLogger(CommonRelayLinkExplorer.class);

	private final Map<Long, AcceptorRelayTunnel<UID>> acceptorTunnelMap = new ConcurrentHashMap<>();

	private final Map<Long, AccessorRelayTunnel<UID>> accessorTunnelMap = new ConcurrentHashMap<>();

	private final NetBootstrapContext<UID> bootstrapContext;

	public CommonRelayLinkExplorer(NetBootstrapContext<UID> bootstrapContext) {
		this.bootstrapContext = bootstrapContext;
	}

	@Override
	public NetTunnel<UID> getTunnel(long tunnelId) {
		return this.stubMap.get(tunnelId);
	}

	@Override
	public NetTunnel<UID> closeTunnel(long tunnelId) {
		NetTunnel<UID> tunnel = this.stubMap.get(tunnelId);
		if (tunnel != null) {
			tunnel.close();
		}
		return tunnel;
	}

	@Override
	public void destroyTunnel(RelayTunnel<UID> tunnel) {
		this.stubMap.remove(tunnel.getId(), tunnel);
		if (!tunnel.isClosed()) {
			tunnel.close();
		}
	}

	protected boolean registerTunnel(NetTunnel<UID> tunnel) {
		NetTunnel<UID> old = stubMap.putIfAbsent(tunnel.getId(), tunnel);
		return old == null || old == tunnel;
	}

	@Override
	public void close() {
		this.stubMap.forEach((id, tunnel) -> tunnel.close());
	}

	@Override
	public NetTunnel<UID> acceptTunnel(long tunnelId, String host, int port) throws PipeClosedException {
		RelayLinkStatus status = this.relayAcceptor.getStatus();
		if (!status.isCloseStatus()) {
			throw new PipeClosedException(format("link {} closed, connectRepeater failed", this.relayAcceptor.getRemoteAddress()));
		}
		NetTunnel<UID> tunnel = createProviderTunnel(tunnelId, host, port);
		synchronized (this) {
			if (status == RelayLinkStatus.CLOSED) {
				throw new PipeClosedException(format("link {} closed, connectRepeater failed", this.relayAcceptor.getRemoteAddress()));
			}
			try {
				if (this.registerTunnel(tunnel)) { // 注册
					tunnel.open();
					this.relayAcceptor.write(new TunnelConnectedPacket(tunnel.getId(), true), false);
				}
			} catch (Throwable e) {
				LOGGER.error("acceptTunnel exception", e);
				this.relayAcceptor.write(new TunnelConnectedPacket(tunnel.getId(), false), false);
				tunnel.close();
			}
		}
		return tunnel;
	}

	@Override
	public boolean bindTunnel(NetTunnel<UID> tunnel) {
		if (this.registerTunnel(tunnel)) { // 注册
			tunnel.open();
			InetSocketAddress address = tunnel.getRemoteAddress();
			InetAddress inetAddress = address.getAddress();
			TunnelConnectArguments arguments = new TunnelConnectArguments(inetAddress.getAddress(), address.getPort());
			this.relayAcceptor.write(new TunnelConnectPacket(tunnel.getId(), arguments), false);
			return true;
		}
		return false;
	}

	private NetTunnel<UID> createProviderTunnel(long tunnelId, String host, int port) {
		InetSocketAddress address = new InetSocketAddress(host, port);
		MessageTransporter<UID> transporter = new RelayLinkMessageTransporter<>(this.relayAcceptor, address);
		return new CommonAcceptorRelayTunnel<>(tunnelId, transporter, this.bootstrapContext);
	}

}
