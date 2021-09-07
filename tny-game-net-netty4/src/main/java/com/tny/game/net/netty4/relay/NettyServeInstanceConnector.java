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

	private final List<NettyRelayLinkConnectMonitor> monitors = new CopyOnWriteArrayList<>();

	private final LocalRelayContext relayContext;

	private final LocalServeClusterContext serveClusterContext;

	private final ScheduledExecutorService executorService;

	NettyServeInstanceConnector(
			LocalRelayContext relayContext, LocalServeClusterContext serveClusterContext, ScheduledExecutorService executorService) {
		this.relayContext = relayContext;
		this.serveClusterContext = serveClusterContext;
		this.executorService = executorService;
	}

	protected void connect(NettyRelayLinkConnectMonitor monitor) {
		serveClusterContext.connect(monitor.getUrl(), monitor);
	}

	protected void connect(NettyRelayLinkConnectMonitor monitor, long delayTime) {
		executorService.schedule(monitor::connect, delayTime, TimeUnit.MILLISECONDS);
	}

	public synchronized void start(LocalServeInstance instance, int connectionSize) {
		if (!this.monitors.isEmpty()) {
			return;
		}
		List<NettyRelayLinkConnectMonitor> monitors = new ArrayList<>();
		for (int i = 0; i < connectionSize; i++) {
			monitors.add(new NettyRelayLinkConnectMonitor(relayContext, instance, this));
		}
		this.monitors.addAll(monitors);
		for (NettyRelayLinkConnectMonitor monitor : monitors) {
			monitor.connect();
		}
	}

	public synchronized void stop() {
		for (NettyRelayLinkConnectMonitor monitor : monitors) {
			monitor.close();
		}
	}

}
