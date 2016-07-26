package com.tny.game.actor.local;

import com.tny.game.LogUtils;
import com.tny.game.common.thread.CoreThreadFactory;
import com.tny.game.worker.CommandBox;
import com.tny.game.worker.CommandExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

/**
 * Actor 执行器
 * Created by Kun Yang on 16/4/26.
 */
class ActorCommandExecutor implements CommandExecutor, ActorWorker {

    private ForkJoinPool pool;

    private String name;

    private static final Logger LOGGER = LoggerFactory.getLogger(LogUtils.WORKER + "-" + ActorCommandExecutor.class.getName());

    private Queue<ActorCommandBox> commandBoxList = new ConcurrentLinkedQueue<>();

    private volatile boolean working = true;

    private ExecutorService executor;

    private long nextRunningTime;

    private volatile long sleepTime;

    public ActorCommandExecutor(String name) {
        this(name, new ForkJoinPool(10));
    }

    public ActorCommandExecutor(String name, ForkJoinPool pool) {
        this.name = name;
        this.pool = pool != null ? pool : ForkJoinPool.commonPool();
        this.start();
    }

    @Override
    public void start() {
        this.executor = Executors.newSingleThreadExecutor(new CoreThreadFactory(this.getName() + "-SubmitThread"));
        this.executor.execute(() -> {
            nextRunningTime = System.currentTimeMillis();
            while (true) {
                try {
                    if (executor.isShutdown())
                        break;
                    commandBoxList.forEach(this::trySubmit);
                    nextRunningTime += 100L;

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

    private void trySubmit(ActorCommandBox box) {
        if (box.trySubmit())
            pool.submit(box);
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
    public boolean takeOver(LocalActor<?, ?> actor) {
        return register(actor.cell().getCommandBox());
    }

    @Override
    public boolean register(CommandBox commandBox) {
        if (commandBox instanceof ActorCommandBox) {
            ActorCommandBox box = (ActorCommandBox) commandBox;
            if (commandBox.bindWorker(new ActorCommandWorker(this))) {
                this.commandBoxList.add(box);
                if (box.trySubmit())
                    pool.submit(box);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean unregister(CommandBox commandBox) {
        if (commandBox instanceof ActorCommandBox) {
            ActorCommandBox box = (ActorCommandBox) commandBox;
            if (this.commandBoxList.remove(box)) {
                commandBox.unbindWorker();
                return true;
            }
        }
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

    static class ActorCommandWorker implements ActorWorker {

        private volatile Thread currentThread;

        private ActorCommandExecutor executor;

        private ActorCommandWorker(ActorCommandExecutor executor) {
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
        public String getName() {
            return executor.getName();
        }

        @Override
        public boolean takeOver(LocalActor<?, ?> actor) {
            return executor.takeOver(actor);
        }

        void trySubmit(ActorCommandBox box) {
            this.executor.trySubmit(box);
        }

        @Override
        public void submit(CommandBox box) {
            this.currentThread = Thread.currentThread();
            try {
                ActorWorker.super.submit(box);
            } finally {
                this.currentThread = null;
            }
        }

    }
}
