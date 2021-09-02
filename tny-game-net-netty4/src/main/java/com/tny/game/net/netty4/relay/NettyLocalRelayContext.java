package com.tny.game.net.netty4.relay;

import com.tny.game.common.concurrent.*;
import com.tny.game.common.url.*;
import com.tny.game.net.base.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.relay.link.route.*;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/30 9:04 下午
 */
public class NettyLocalRelayContext implements LocalRelayContext {

	private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1,
			new CoreThreadFactory("RelayReconnectScheduled"));

	private final NetAppContext appContext;

	private final RelayMessageRouter relayMessageRouter;

	private final RelayClientGuide guide;

	private final String launchId;

	private final AtomicLong indexCounter = new AtomicLong();

	public NettyLocalRelayContext(NetAppContext appContext, RelayClientGuide guide, RelayMessageRouter relayMessageRouter) {
		this.guide = guide;
		this.appContext = appContext;
		this.relayMessageRouter = relayMessageRouter;
		this.launchId = this.getClusterId() + "." + this.getInstanceId() + "." +
				System.nanoTime() + "." + ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
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
	public String createId() {
		return UUID.nameUUIDFromBytes((this.launchId + "#" + indexCounter.incrementAndGet()).getBytes(StandardCharsets.UTF_8)).toString();
	}

	@Override
	public RelayMessageRouter getRelayMessageRouter() {
		return relayMessageRouter;
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
	public void connect(URL url, RelayConnectCallback callback, long delayTime) {
		executorService.schedule(() -> guide.connect(url, callback), delayTime, TimeUnit.MILLISECONDS);
	}

}
