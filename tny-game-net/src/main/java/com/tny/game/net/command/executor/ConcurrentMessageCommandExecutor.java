package com.tny.game.net.command.executor;

import com.tny.game.common.worker.command.*;
import com.tny.game.net.base.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

import java.util.Queue;
import java.util.concurrent.*;

public class ConcurrentMessageCommandExecutor extends ForkJoinMessageCommandExecutor {

    private static final Logger LOG_NET = LoggerFactory.getLogger(NetLogger.EXECUTOR);

    private static Queue<Command> scheduledChildExecutors = new ConcurrentLinkedQueue<>();

    public ConcurrentMessageCommandExecutor() {
        super();
    }

    public ConcurrentMessageCommandExecutor(int threads) {
        super(threads);
    }

    public ConcurrentMessageCommandExecutor(int threads, int childDelayTime) {
        super(threads, childDelayTime);
    }

    @Override
    public void prepareStart() {
        super.prepareStart();
        scheduledExecutor().scheduleAtFixedRate(() -> {
            while (!scheduledChildExecutors.isEmpty()) {
                Command command = scheduledChildExecutors.poll();
                if (command != null)
                    doSubmit(command);
            }
        }, this.nextInterval, this.nextInterval, TimeUnit.MILLISECONDS);
        scheduledExecutor().scheduleAtFixedRate(() ->
                        LOG_NET.info("[scheduledChildExecutors ] [executorService size : {} | Parallelism : {} | PoolSize : {}]",
                                scheduledChildExecutors.size(),
                                executorService.getParallelism(), executorService.getPoolSize()),
                15000, 15000, TimeUnit.MILLISECONDS);
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
