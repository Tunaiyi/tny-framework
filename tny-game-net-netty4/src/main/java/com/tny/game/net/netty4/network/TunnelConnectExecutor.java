package com.tny.game.net.netty4.network;

import java.util.concurrent.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/8 6:48 下午
 */
public interface TunnelConnectExecutor extends Executor {

	ScheduledFuture<Void> schedule(Runnable runnable, long delay, TimeUnit milliseconds);

}
