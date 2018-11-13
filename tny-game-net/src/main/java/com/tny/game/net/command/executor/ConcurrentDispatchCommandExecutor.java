package com.tny.game.net.command.executor;

import com.tny.game.common.unit.annotation.Unit;
import com.tny.game.common.worker.command.Command;
import com.tny.game.net.base.NetLogger;
import com.tny.game.net.transport.NetTunnel;
import org.slf4j.*;

import java.util.Queue;
import java.util.concurrent.*;

@Unit
public class ConcurrentDispatchCommandExecutor extends ThreadPoolCommandExecutor {

    private static final Logger LOG_NET = LoggerFactory.getLogger(NetLogger.EXECUTOR);

    private static Queue<Command> scheduledChildExecutors = new ConcurrentLinkedQueue<>();

    public ConcurrentDispatchCommandExecutor() {
    }

    public ConcurrentDispatchCommandExecutor(int threads) {
        super(threads);
    }

    @Override
    public void prepareStart() {
        super.prepareStart();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            while (!scheduledChildExecutors.isEmpty()) {
                Command command = scheduledChildExecutors.poll();
                if (command != null)
                    doSubmit(command);
            }
        }, this.childDelayTime, this.childDelayTime, TimeUnit.MILLISECONDS);
        scheduledExecutorService.scheduleAtFixedRate(() -> LOG_NET.info("[scheduledChildExecutors size {}] [executorService size : {} | Parallelism : {} | PoolSize : {}]",
                scheduledChildExecutors.size(), scheduledChildExecutors.size(), this.threads, this.maxThreads), 15000, 15000, TimeUnit.MILLISECONDS);
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
