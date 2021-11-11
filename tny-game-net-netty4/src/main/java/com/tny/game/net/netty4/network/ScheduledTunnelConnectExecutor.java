package com.tny.game.net.netty4.network;

import java.util.concurrent.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/8 6:54 下午
 */
public class ScheduledTunnelConnectExecutor implements TunnelConnectExecutor {

	private final ScheduledExecutorService executorService;

	public ScheduledTunnelConnectExecutor(ScheduledExecutorService executorService) {
		this.executorService = executorService;
	}

	@Override
	public ScheduledFuture<Void> schedule(Runnable runnable, long delay, TimeUnit timeUnit) {
		return as(executorService.schedule(runnable, delay, timeUnit));
	}

	@Override
	public void execute(Runnable runnable) {
		executorService.execute(runnable);
	}

}
