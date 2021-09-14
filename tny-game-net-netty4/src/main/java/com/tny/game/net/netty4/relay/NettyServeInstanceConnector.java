package com.tny.game.net.netty4.relay;

import com.tny.game.net.relay.link.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/30 9:09 下午
 */
class NettyServeInstanceConnector {

	private final List<NettyRelayLinkConnector> connectors = new CopyOnWriteArrayList<>();

	private final LocalRelayContext relayContext;

	private final LocalServeClusterContext serveClusterContext;

	private final ScheduledExecutorService executorService;

	NettyServeInstanceConnector(
			LocalRelayContext relayContext, LocalServeClusterContext serveClusterContext, ScheduledExecutorService executorService) {
		this.relayContext = relayContext;
		this.serveClusterContext = serveClusterContext;
		this.executorService = executorService;
	}

	protected void connect(NettyRelayLinkConnector monitor) {
		serveClusterContext.connect(monitor.getUrl(), monitor);
	}

	protected void connect(NettyRelayLinkConnector monitor, long delayTime) {
		executorService.schedule(monitor::connect, delayTime, TimeUnit.MILLISECONDS);
	}

	public synchronized void start(NetLocalServeInstance instance, int connectionSize) {
		if (!this.connectors.isEmpty()) {
			return;
		}
		List<NettyRelayLinkConnector> monitors = new ArrayList<>();
		for (int i = 0; i < connectionSize; i++) {
			monitors.add(new NettyRelayLinkConnector(relayContext, instance, this));
		}
		this.connectors.addAll(monitors);
		for (NettyRelayLinkConnector connector : monitors) {
			connector.connect();
		}
	}

	public synchronized void stop() {
		for (NettyRelayLinkConnector monitor : connectors) {
			monitor.close();
		}
		connectors.clear();
	}

}
