package com.tny.game.worker;

import com.tny.game.LogUtils;
import com.tny.game.common.thread.CoreThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FrequencyCommandExecutor implements CommandExecutor {

    protected static final Logger LOGGER = LoggerFactory.getLogger(LogUtils.WORKER);

    /**
     * 名字
     */
    protected String name;

    /**
     * 是否停止
     */
    protected volatile boolean working = true;

    /**
     * 停止时间
     */
    protected long stopTime = 0;

    /**
     * 当前线程
     */
    protected Thread currentThread;

    protected Queue<CommandBox> commandBoxList = new ConcurrentLinkedQueue<>();

    private ExecutorService executor;

    private long nextRunningTime;

    private volatile long totalRunningTime;

    private volatile long totalSleepTime;

    private volatile long totalRunSize;

    private volatile long sleepTime;

    private volatile long runningTime;

    private volatile int runSize;

    private volatile int continueTime;

    private CommandWorker worker = new CommandWorker() {

        @Override
        public boolean isOnCurrentThread() {
            return currentThread == Thread.currentThread();
        }

        @Override
        public boolean isWorking() {
            return FrequencyCommandExecutor.this.isWorking();
        }

        @Override
        public boolean register(CommandBox commandBox) {
            return FrequencyCommandExecutor.this.register(commandBox);
        }

        @Override
        public boolean unregister(CommandBox commandBox) {
            return FrequencyCommandExecutor.this.register(commandBox);
        }

    };

    public FrequencyCommandExecutor(String name) {
        this.name = name;
    }

    public void stop() {
        if (!working)
            return;
        working = false;
        stopTime = System.currentTimeMillis();
    }


    @Override
    public void start() {
        this.executor = Executors.newSingleThreadExecutor(new CoreThreadFactory(this.name, true));
        this.executor.execute(() -> {
            nextRunningTime = System.currentTimeMillis();
            currentThread = Thread.currentThread();
            while (true) {
                try {
                    if (executor.isShutdown())
                        break;
                    long currentTime = System.currentTimeMillis();
                    int currentRunSize = 0;
                    int currentContinueTime = 0;
                    while (currentTime >= nextRunningTime) {
                        for (CommandBox box : commandBoxList) {
                            this.worker.submit(box);
                            // box.getProcessUseTime();
                            // currentRunSize += box.getProcessSize();
                        }
                        nextRunningTime += 100L;
                        currentContinueTime++;
                        currentTime = System.currentTimeMillis();
                    }
                    if (commandBoxList.isEmpty() && !working)
                        return;

                    continueTime = currentContinueTime;
                    runSize = currentRunSize;
                    totalRunSize += runSize;
                    if (totalRunSize < 0)
                        totalRunSize = 0;

                    long stopTime1 = System.currentTimeMillis();
                    runningTime = stopTime1 - currentTime;

                    sleepTime = nextRunningTime - stopTime1;
                    sleepTime = sleepTime < 0 ? 0 : sleepTime;

                    totalRunningTime += runningTime;
                    totalSleepTime += sleepTime;
                    if (sleepTime > 0) {
                        Thread.sleep(sleepTime);
                    }
                } catch (InterruptedException e) {
                    LOGGER.warn("InterruptedException by FrequencyWorker " + Thread.currentThread().getName(), e);
                } catch (Exception e) {
                    LOGGER.warn("Exception by FrequencyWorker " + Thread.currentThread().getName(), e);
                }
            }
        });
    }

    @Override
    public void shutdown() {
        stop();
        executor.shutdown();
    }

    @Override
    public String toString() {
        long totalSleepTime = this.totalSleepTime;
        long totalRunningTime = this.totalRunningTime;
        long sleepTime = this.sleepTime;
        long runningTime = this.runningTime;
        int continueTime = this.continueTime;
        return new StringBuilder()
                .append(this.getName())
                .append(" #任务数量: ")
                .append(size())
                .append(" #附加任务箱数量: ")
                .append(this.commandBoxList.size())
                .append(" #总运行数量")
                .append(totalRunSize)
                .append(" #最近运行数量")
                .append(runSize)
                .append(" #最近连续次数: ")
                .append(continueTime)
                .append(" #最近休眠时间: ")
                .append(sleepTime)
                .append(" #最近运行时间")
                .append(runningTime)
                .append(" #最近休眠比率: ")
                .append((double) sleepTime / (double) (sleepTime + runningTime))
                .append(" #休眠总时间: ")
                .append(totalSleepTime)
                .append(" #运行总时间: ")
                .append(totalRunningTime)
                .append(" #总休眠比率: ")
                .append((double) totalSleepTime / (double) (totalSleepTime + totalRunningTime))
                .toString();
    }

    @Override
    public int size() {
        int size = 0;
        for (CommandBox commandBox : commandBoxList)
            size += commandBox.size();
        return size;
    }

    @Override
    public boolean register(CommandBox commandBox) {
        if (commandBox instanceof WorkerCommandBox) {
            WorkerCommandBox<?, ?> workerCommandBox = (WorkerCommandBox<?, ?>) commandBox;
            if (workerCommandBox.bindWorker(this.worker)) {
                this.commandBoxList.add(workerCommandBox);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean unregister(CommandBox commandBox) {
        if (commandBox instanceof WorkerCommandBox) {
            WorkerCommandBox<?, ?> workerCommandBox = (WorkerCommandBox<?, ?>) commandBox;
            if (this.commandBoxList.remove(workerCommandBox)) {
                workerCommandBox.unbindWorker();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isWorking() {
        return this.working;
    }

    @Override
    public String getName() {
        return name;
    }

}
