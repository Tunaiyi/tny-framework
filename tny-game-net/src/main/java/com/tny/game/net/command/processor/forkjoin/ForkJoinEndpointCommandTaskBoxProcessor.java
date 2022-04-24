package com.tny.game.net.command.processor.forkjoin;

import com.tny.game.common.concurrent.*;
import com.tny.game.common.concurrent.collection.*;
import com.tny.game.common.lifecycle.*;
import com.tny.game.net.command.processor.*;
import com.tny.game.net.command.task.*;

import java.util.Set;
import java.util.concurrent.*;

/**
 * @author KGTny
 */
public class ForkJoinEndpointCommandTaskBoxProcessor extends EndpointCommandTaskBoxProcessor<CommandTaskBoxDriver> implements AppPrepareStart {

    private static final Set<CommandTaskBoxDriver> SCHEDULED_PROCESSORS = new ConcurrentHashSet<>();

    private final ForkJoinEndpointCommandTaskProcessorSetting setting;

    private ExecutorService executorService;

    private final ScheduledExecutorService scheduledExecutorService = ThreadPoolExecutors
            .scheduled(this.getClass().getSimpleName() + "Scheduled", 1, true);

    public ForkJoinEndpointCommandTaskBoxProcessor(ForkJoinEndpointCommandTaskProcessorSetting setting) {
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
    protected CommandTaskBoxDriver createDriver(CommandTaskBox box) {
        return new CommandTaskBoxDriver(box, this);
    }

    private void scheduleProcessor() {
        for (CommandTaskBoxDriver processor : SCHEDULED_PROCESSORS) {
            SCHEDULED_PROCESSORS.remove(processor);
            processor.trySubmit();
        }
    }

    public void shutdown() {
        this.executorService.shutdown();
    }

    @Override
    public void execute(CommandTaskBoxDriver driver) {
        this.executorService.execute(driver);
    }

    @Override
    public void schedule(CommandTaskBoxDriver driver) {
        SCHEDULED_PROCESSORS.add(driver);
    }

}
