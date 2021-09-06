package com.tny.game.net.netty4.relay;

import com.tny.game.net.base.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.relay.link.route.*;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/30 9:04 下午
 */
public class NettyLocalRelayContext implements LocalRelayContext {

	private String launchId;

	private NetAppContext appContext;

	private RelayMessageRouter relayMessageRouter;

	private ServeClusterFilter serveClusterFilter;

	private final AtomicLong indexCounter = new AtomicLong();

	public NettyLocalRelayContext(NetAppContext appContext, RelayMessageRouter relayMessageRouter, ServeClusterFilter serveClusterFilter) {
		this.setAppContext(appContext);
		this.relayMessageRouter = relayMessageRouter;
		this.serveClusterFilter = serveClusterFilter;
	}

	@Override
	public String getClusterId() {
		return appContext.getAppType();
	}

	@Override
	public long getInstanceId() {
		return appContext.getServerId();
	}

	@Override
	public String createLinkId() {
		return UUID.nameUUIDFromBytes((this.launchId + "#" + indexCounter.incrementAndGet()).getBytes(StandardCharsets.UTF_8)).toString();
	}

	@Override
	public RelayMessageRouter getRelayMessageRouter() {
		return relayMessageRouter;
	}

	@Override
	public ServeClusterFilter getServeClusterFilter() {
		return serveClusterFilter;
	}

	@Override
	public NetAppContext getAppContext() {
		return appContext;
	}

	public NettyLocalRelayContext setAppContext(NetAppContext appContext) {
		this.appContext = appContext;
		this.launchId = this.getClusterId() + "." + this.getInstanceId() + "." +
				System.nanoTime() + "." + ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
		return this;
	}

	public NettyLocalRelayContext setRelayMessageRouter(RelayMessageRouter relayMessageRouter) {
		this.relayMessageRouter = relayMessageRouter;
		return this;
	}

	public NettyLocalRelayContext setLaunchId(String launchId) {
		this.launchId = launchId;
		return this;
	}

	public NettyLocalRelayContext setServeClusterFilter(ServeClusterFilter serveClusterFilter) {
		this.serveClusterFilter = serveClusterFilter;
		return this;
	}

}
