package com.tny.game.net.command.executor;

import com.tny.game.common.concurrent.*;
import com.tny.game.common.config.Config;
import com.tny.game.common.worker.command.Command;
import com.tny.game.net.base.NetLogger;
import com.tny.game.net.base.annotation.Unit;
import com.tny.game.net.command.DispatchCommandExecutor;
import com.tny.game.net.transport.listener.SessionHolderListener;
import com.tny.game.net.transport.NetTunnel;
import com.tny.game.net.utils.NetConfigs;
import org.slf4j.*;

import java.util.Queue;
import java.util.concurrent.*;

@Unit("ConcurrentDispatchCommandExecutor")
public class ConcurrentDispatchCommandExecutor implements DispatchCommandExecutor, SessionHolderListener {

    private static final Logger LOG_NET = LoggerFactory.getLogger(NetLogger.EXECUTOR);

    private static final String POOL_NAME = "ConcurrentDispatchCommandExecutor";

    private ForkJoinPool executorService;

    private static ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new CoreThreadFactory("DispatchCommandScheduledExecutor"));

    private static Queue<Command> scheduledChildExecutors = new ConcurrentLinkedQueue<>();

    public ConcurrentDispatchCommandExecutor(int threads) {
        init(threads);
    }

    public ConcurrentDispatchCommandExecutor(Config config) {
        int threads = config.getInt(NetConfigs.DISPATCHER_EXECUTOR_THREADS, Runtime.getRuntime().availableProcessors() * 2);
        this.init(threads);
    }

    private void init(int threads) {
        this.executorService = ForkJoinPools.pool(threads, POOL_NAME);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            while (!scheduledChildExecutors.isEmpty()) {
                Command command = scheduledChildExecutors.poll();
                if (command != null)
                    doSubmit(command);
            }
        }, 31, 31, TimeUnit.MILLISECONDS);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            LOG_NET.info("[scheduledChildExecutors size {}] [executorService size : {} | Parallelism : {} | PoolSize : {}]", scheduledChildExecutors.size(),
                    executorService.getQueuedSubmissionCount(), executorService.getParallelism(), executorService.getPoolSize());
        }, 15000, 15000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void submit(NetTunnel<?> tunnel, Command command) {
        doSubmit(command);
    }

    private void doSubmit(Command command) {
        executorService.submit(() -> {
            try {
                command.execute();
            } catch (Throwable e) {
                LOG_NET.error("run command task {} exception", command.getName(), e);
            }
            if (!command.isDone()) {
                scheduledChildExecutors.add(command);
            }
        });
    }

    @Override
    public void shutdown() {
        this.executorService.shutdown();
    }
}
