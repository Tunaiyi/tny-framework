package com.tny.game.net.netty4.relay;

import com.tny.game.common.concurrent.*;
import com.tny.game.common.url.*;
import com.tny.game.net.base.*;
import com.tny.game.net.relay.link.*;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/30 8:49 下午
 */
public class NettyLocalServeCluster extends BaseLocalServeCluster<NettyServeInstance> {

	private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1,
			new CoreThreadFactory("RelayReconnectScheduled"));

	private final NetAppContext appContext;

	private final RelayClientGuide guide;

	private final String launchId;

	private final AtomicLong indexCounter = new AtomicLong();

	public NettyLocalServeCluster(RelayServeClusterContext clusterContext, NetAppContext appContext) {
		super(clusterContext.getId(), clusterContext.getServeInstanceAllotStrategy(), clusterContext.getRelayLinkAllotStrategy());
		this.appContext = appContext;
		this.setInstanceFactory((c, node) -> new NettyServeInstance(this, node, clusterContext.getConnectionSize()));
		this.guide = clusterContext.getClientGuide();
		this.launchId = appContext.getAppType() + "." + appContext.getServerId() + "." +
				System.nanoTime() + "." + ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
	}

	public String getLocalClusterId() {
		return appContext.getAppType();
	}

	public long getLocalInstanceId() {
		return appContext.getServerId();
	}

	String createId() {
		return UUID.nameUUIDFromBytes((this.launchId + "#" + indexCounter.incrementAndGet()).getBytes(StandardCharsets.UTF_8)).toString();
	}

	/**
	 * @param url url
	 */
	public void connect(URL url, RelayConnectCallback callback) {
		guide.connect(url, callback);
	}

	/**
	 * @param url url
	 */
	public void connect(URL url, long delayTime, RelayConnectCallback callback) {
		executorService.schedule(() -> guide.connect(url, callback), delayTime, TimeUnit.MILLISECONDS);
	}

}
