package com.tny.game.net.command.executor;

import com.tny.game.common.concurrent.*;
import com.tny.game.common.lifecycle.*;

import java.util.concurrent.*;

/**
 * @author KGTny
 */
public abstract class ForkJoinMessageCommandExecutor implements MessageCommandExecutor, AppPrepareStart {

    /**
     * 间歇时间
     */
    public static final int DEFAULT_NEXT_INTERVAL = 31;

    /* 线程数 */
    private int threads;

    /* ChildExecutor command 未完成, 延迟时间*/
    protected int nextInterval;

    protected ForkJoinPool executorService;

    protected ScheduledExecutorService scheduledExecutorService = ThreadPoolExecutors.scheduled(this.getClass().getSimpleName() + "Scheduled", 4, true);

    public ForkJoinMessageCommandExecutor() {
        this(Runtime.getRuntime().availableProcessors(), DEFAULT_NEXT_INTERVAL);
    }

    public ForkJoinMessageCommandExecutor(int threads) {
        this(threads, DEFAULT_NEXT_INTERVAL);
    }

    public ForkJoinMessageCommandExecutor(int threads, int nextInterval) {
        this.threads = threads;
        this.nextInterval = nextInterval;
    }

    @Override
    public void shutdown() {
        this.executorService.shutdown();
    }

    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), LifecycleLevel.SYSTEM_LEVEL_7);
    }

    @Override
    public void prepareStart() {
        this.executorService = ForkJoinPools.pool(this.threads, this.getClass().getSimpleName(), true);
        // ThreadPoolExecutors.pool(this.getClass().getSimpleName(), this.threads, Math.max(this.threads, this.maxThreads), keepsAlive);
    }

}
