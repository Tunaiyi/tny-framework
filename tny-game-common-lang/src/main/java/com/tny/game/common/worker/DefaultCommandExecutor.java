package com.tny.game.common.worker;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.tny.game.common.concurrent.*;
import com.tny.game.common.utils.*;
import org.slf4j.*;

import java.util.Queue;
import java.util.concurrent.*;

/**
 * Created by Kun Yang on 2017/6/12.
 */
public class DefaultCommandExecutor implements CommandExecutor, CommandWorker {

    private ExecutorService executor;

    private String name;

    private static final Logger LOGGER = LoggerFactory.getLogger(LogAide.WORKER + "-" + DefaultCommandExecutor.class.getName());

    private Queue<CommandBox> commandBoxList = new ConcurrentLinkedQueue<>();

    private volatile boolean working = true;

    private ExecutorService hearbeatExecutor;

    private long hearbeatInterval;

    private long nextRunningTime;

    private volatile long lastSleepTime;

    public DefaultCommandExecutor(String name) {
        this(name, 12);
    }

    public DefaultCommandExecutor(String name, ExecutorService executor) {
        this(name, 12, executor);
    }

    public DefaultCommandExecutor(String name, int frequency) {
        this(name, frequency, new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
                Runtime.getRuntime().availableProcessors() * 2,
                30, TimeUnit.SECONDS,
                new LinkedTransferQueue<>(),
                new ThreadFactoryBuilder()
                        .setNameFormat("DefaultCommandExecutor-%d")
                        .build()));
    }

    public DefaultCommandExecutor(String name, int frequency, ExecutorService executor) {
        this.name = name;
        this.executor = executor;
        this.hearbeatInterval = Math.max(1000 / frequency, 5);
        this.start();
    }

    @Override
    public void start() {
        this.hearbeatExecutor = Executors.newSingleThreadExecutor(new CoreThreadFactory(this.getName() + "-SubmitThread", true));
        this.hearbeatExecutor.execute(() -> {
            this.nextRunningTime = System.currentTimeMillis();
            while (true) {
                try {
                    if (this.hearbeatExecutor.isShutdown()) {
                        break;
                    }
                    this.commandBoxList.forEach(box -> {
                        if (!box.isEmpty()) {
                            box.submit();
                        }
                    });
                    this.nextRunningTime += this.hearbeatInterval;

                    long finishAt = System.currentTimeMillis();

                    long sleepTime = this.nextRunningTime - finishAt;
                    sleepTime = sleepTime < 0 ? 0 : sleepTime;

                    if (sleepTime > 0) {
                        Thread.sleep(sleepTime);
                    }
                    this.lastSleepTime = sleepTime;
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
        return this.name;
    }

    @Override
    public void shutdown() {
        stop();
        this.hearbeatExecutor.shutdown();
    }

    @Override
    public String toString() {
        long sleepTime = this.lastSleepTime;
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
        for (CommandBox commandBox : this.commandBoxList)
            size += commandBox.size();
        return size;
    }

    @Override
    public boolean isOnCurrentThread() {
        return false;
    }

    @Override
    public boolean register(CommandBox commandBox) {
        if (commandBox.bindWorker(new BindCommandWorker(this))) {
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
        return this.working;
    }

    @Override
    public void stop() {
        if (!this.working) {
            return;
        }
        this.working = false;
    }

    static class BindCommandWorker implements CommandWorker {

        private volatile Thread currentThread;

        private final DefaultCommandExecutor executor;

        private BindCommandWorker(DefaultCommandExecutor executor) {
            this.executor = executor;
        }

        @Override
        public boolean isOnCurrentThread() {
            return this.currentThread == Thread.currentThread();
        }

        @Override
        public boolean isWorking() {
            return this.executor.isWorking();
        }

        @Override
        public boolean execute(CommandBox commandBox) {
            this.executor.executor.submit(() -> doProcess(commandBox));
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
