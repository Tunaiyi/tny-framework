package com.tny.game.net.command.executor;

import com.tny.game.common.concurrent.*;
import com.tny.game.common.lifecycle.*;

import java.util.concurrent.*;

/**
 * @author KGTny
 */
public abstract class ForkJoinCommandTaskExecutor implements AppPrepareStart {

    /**
     * 间歇时间
     */
    private static final int DEFAULT_NEXT_INTERVAL = 8;

    /* 线程数 */
    private final int threads;

    /* ChildExecutor command 未完成, 延迟时间*/
    protected int nextInterval;

    protected ExecutorService executorService;

    private final ScheduledExecutorService scheduledExecutorService = ThreadPoolExecutors
            .scheduled(this.getClass().getSimpleName() + "Scheduled", 4, true);

    public ForkJoinCommandTaskExecutor() {
        this(Runtime.getRuntime().availableProcessors(), DEFAULT_NEXT_INTERVAL);
    }

    public ForkJoinCommandTaskExecutor(int threads) {
        this(threads, DEFAULT_NEXT_INTERVAL);
    }

    public ForkJoinCommandTaskExecutor(int threads, int nextInterval) {
        this.threads = threads;
        this.nextInterval = nextInterval;
    }

    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), LifecycleLevel.SYSTEM_LEVEL_7);
    }

    @Override
    public void prepareStart() {
        if (this.executorService == null) {
            this.executorService = ForkJoinPools.pool(this.threads, this.getClass().getSimpleName(), true);
        }
    }

    public void shutdown() {
        this.executorService.shutdown();
    }

    protected ScheduledExecutorService scheduledExecutor() {
        return this.scheduledExecutorService;
    }

}
