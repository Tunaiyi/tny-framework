package com.tny.game.common.worker;

import com.tny.game.common.utils.Logs;
import com.tny.game.common.thread.CoreThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

/**
 * Created by Kun Yang on 2017/6/12.
 */
public class ForkJoinPoolCommandExecutor implements CommandExecutor, CommandWorker {

    private ForkJoinPool pool;

    private String name;

    private static final Logger LOGGER = LoggerFactory.getLogger(Logs.WORKER + "-" + ForkJoinPoolCommandExecutor.class.getName());

    private Queue<CommandBox> commandBoxList = new ConcurrentLinkedQueue<>();

    private volatile boolean working = true;

    private ExecutorService executor;

    private long nextRunningTime;

    private volatile long sleepTime;

    public ForkJoinPoolCommandExecutor(String name) {
        this(name, ForkJoinPool.commonPool());
    }

    public ForkJoinPoolCommandExecutor(String name, ForkJoinPool pool) {
        this.name = name;
        this.pool = pool != null ? pool : ForkJoinPool.commonPool();
        this.start();
    }

    @Override
    public void start() {
        this.executor = Executors.newSingleThreadExecutor(new CoreThreadFactory(this.getName() + "-SubmitThread", true));
        this.executor.execute(() -> {
            nextRunningTime = System.currentTimeMillis();
            while (true) {
                try {
                    if (executor.isShutdown())
                        break;
                    commandBoxList.forEach(box -> {
                        if (!box.isEmpty())
                            box.submit();
                    });
                    nextRunningTime += 84L;

                    long finishAt = System.currentTimeMillis();

                    sleepTime = nextRunningTime - finishAt;
                    sleepTime = sleepTime < 0 ? 0 : sleepTime;

                    if (sleepTime > 0) {
                        Thread.sleep(sleepTime);
                    }
                } catch (InterruptedException e) {
                    LOGGER.warn("InterruptedException by ActorCommandExecutor " + Thread.currentThread().getName(), e);
                } catch (Exception e) {
                    LOGGER.warn("Exception by ActorCommandExecutor " + Thread.currentThread().getName(), e);
                }
            }
        });
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void shutdown() {
        stop();
        executor.shutdown();
    }

    @Override
    public String toString() {
        long sleepTime = this.sleepTime;
        return this.getName() +
                " #任务数量: " +
                size() +
                " #附加任务箱数量: " +
                this.commandBoxList.size() +
                " #最近休眠时间: " +
                sleepTime;
    }

    @Override
    public int size() {
        int size = 0;
        for (CommandBox commandBox : commandBoxList)
            size += commandBox.size();
        return size;
    }

    @Override
    public boolean isOnCurrentThread() {
        return false;
    }

    @Override
    public boolean register(CommandBox commandBox) {
        if (commandBox.bindWorker(new ForkJoinPoolCommandWorker(this))) {
            this.commandBoxList.add(commandBox);
            commandBox.submit();
        }
        return true;
    }

    @Override
    public boolean unregister(CommandBox commandBox) {
        if (this.commandBoxList.remove(commandBox)) {
            commandBox.unbindWorker();
            return true;
        }
        return false;
    }

    @Override
    public boolean execute(CommandBox commandBox) {
        return false;
    }

    @Override
    public boolean isWorking() {
        return working;
    }

    @Override
    public void stop() {
        if (!working)
            return;
        working = false;
    }

    static class ForkJoinPoolCommandWorker implements CommandWorker {

        private volatile Thread currentThread;

        private ForkJoinPoolCommandExecutor executor;

        private ForkJoinPoolCommandWorker(ForkJoinPoolCommandExecutor executor) {
            this.executor = executor;
        }

        @Override
        public boolean isOnCurrentThread() {
            return currentThread == Thread.currentThread();
        }

        @Override
        public boolean isWorking() {
            return executor.isWorking();
        }

        @Override
        public boolean execute(CommandBox commandBox) {
            executor.pool.submit(() -> doProcess(commandBox));
            return true;
        }

        public void doProcess(CommandBox box) {
            this.currentThread = Thread.currentThread();
            try {
                box.process();
            } finally {
                this.currentThread = null;
            }
        }

    }
}
