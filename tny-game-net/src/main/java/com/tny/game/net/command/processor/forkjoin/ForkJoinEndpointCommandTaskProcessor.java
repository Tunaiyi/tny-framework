package com.tny.game.net.command.processor.forkjoin;

import com.tny.game.common.concurrent.*;
import com.tny.game.common.concurrent.collection.*;
import com.tny.game.common.lifecycle.*;
import com.tny.game.net.command.processor.*;
import com.tny.game.net.endpoint.task.*;

import java.util.Set;
import java.util.concurrent.*;

/**
 * @author KGTny
 */
public class ForkJoinEndpointCommandTaskProcessor extends EndpointCommandTaskProcessor<EndpointCommandTaskTrigger> implements AppPrepareStart {

    private static final Set<EndpointCommandTaskTrigger> SCHEDULED_PROCESSORS = new ConcurrentHashSet<>();

    /**
     * 间歇时间
     */
    private static final int DEFAULT_NEXT_INTERVAL = 8;
    /**
     * 间歇时间
     */
    private static final int DEFAULT_THREADS = Runtime.getRuntime().availableProcessors();

    private ForkJoinEndpointCommandTaskProcessorSetting setting;

    private ExecutorService executorService;

    private final ScheduledExecutorService scheduledExecutorService = ThreadPoolExecutors
            .scheduled(this.getClass().getSimpleName() + "Scheduled", 1, true);

    public ForkJoinEndpointCommandTaskProcessor(ForkJoinEndpointCommandTaskProcessorSetting setting) {
        this.setting = setting;
    }

    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), LifecycleLevel.SYSTEM_LEVEL_7);
    }

    @Override
    public void prepareStart() {
        if (this.executorService == null) {
            this.executorService = ForkJoinPools.pool(this.setting.getThreads(), this.getClass().getSimpleName(), true);
        }
        long nextInterval = this.setting.getNextInterval();
        this.scheduledExecutorService.scheduleAtFixedRate(this::scheduleProcessor, nextInterval, nextInterval, TimeUnit.MILLISECONDS);
    }

    @Override
    protected EndpointCommandTaskTrigger createTrigger(CommandTaskBox box) {
        return new EndpointCommandTaskTrigger(box, this);
    }

    private void scheduleProcessor() {
        for (EndpointCommandTaskTrigger processor : SCHEDULED_PROCESSORS) {
            SCHEDULED_PROCESSORS.remove(processor);
            processor.trySubmit();
        }
    }

    public void shutdown() {
        this.executorService.shutdown();
    }

    @Override
    protected void process(EndpointCommandTaskTrigger processor) {
        this.executorService.execute(processor);
    }

    @Override
    protected void schedule(EndpointCommandTaskTrigger processor) {
        SCHEDULED_PROCESSORS.add(processor);
    }

}
