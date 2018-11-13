package com.tny.game.net.command.executor;

import com.tny.game.common.concurrent.ThreadPoolExecutors;
import com.tny.game.common.lifecycle.*;

import java.util.concurrent.*;

/**
 * @author KGTny
 */
public abstract class ThreadPoolCommandExecutor implements DispatchCommandExecutor, AppPrepareStart {

    /* 线程数 */
    protected int threads = Runtime.getRuntime().availableProcessors();
    /* 最大线程数 */
    protected int maxThreads = 0;
    /* 线程存活时间 */
    protected long keepsAlive = 60000;
    /* ChildExecutor command 未完成, 延迟时间*/
    protected int childDelayTime = 31;

    protected ExecutorService executorService;

    protected ScheduledExecutorService scheduledExecutorService = ThreadPoolExecutors.scheduled(this.getClass().getSimpleName() + "Scheduled", 1, true);

    public ThreadPoolCommandExecutor() {
    }

    public ThreadPoolCommandExecutor(int threads) {
        this.threads = threads;
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
        this.executorService = ThreadPoolExecutors.pool(this.getClass().getSimpleName(), this.threads, Math.max(this.threads, this.maxThreads), keepsAlive);
    }

}
