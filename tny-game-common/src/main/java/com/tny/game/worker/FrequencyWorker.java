package com.tny.game.worker;

import com.tny.game.LogUtils;
import com.tny.game.common.thread.CoreThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FrequencyWorker implements WorldWorker {

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

    protected CommandBox commandBox = new CoppyWorkerCommandBox();

    protected Queue<CommandBox> commandBoxList = new ConcurrentLinkedQueue<CommandBox>();

    private ExecutorService executor;

    private long nextRunningTime;

    private volatile long totalRunningTime;

    private volatile long totalSleepTime;

    private volatile long totalRunSize;

    private volatile long sleepTime;

    private volatile long runningTime;

    private volatile int runSize;

    private volatile int continueTime;

    public FrequencyWorker(String name) {
        this.name = name;
        this.register(commandBox);
    }

    public void stop() {
        if (!working)
            return;
        working = false;
        stopTime = System.currentTimeMillis();
    }

    @Override
    public void start() {
        this.executor = Executors.newSingleThreadExecutor(new CoreThreadFactory(this.name));
        this.executor.execute(new Runnable() {

            @Override
            public void run() {
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
                            for (CommandBox commandBox : commandBoxList) {
                                commandBox.run();
                                commandBox.getRunUseTime();
                                currentRunSize += commandBox.getRunSize();
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

                        long stopTime = System.currentTimeMillis();
                        runningTime = stopTime - currentTime;

                        sleepTime = nextRunningTime - stopTime;
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
            }

        });
    }

    @Override
    public void release() {
        stop();
        executor.shutdown();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(100);
        long totalSleepTime = this.totalSleepTime;
        long totalRunningTime = this.totalRunningTime;
        long sleepTime = this.sleepTime;
        long runningTime = this.runningTime;
        int continueTime = this.continueTime;
        return builder.append(this.getName())
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
            WorkerCommandBox workerCommandBox = (WorkerCommandBox) commandBox;
            workerCommandBox.bindWorker(this);
            this.commandBoxList.add(workerCommandBox);
            return true;
        }
        return false;
    }

    @Override
    public boolean unregister(CommandBox commandBox) {
        if (commandBox instanceof WorkerCommandBox) {
            WorkerCommandBox workerCommandBox = (WorkerCommandBox) commandBox;
            workerCommandBox.unbindWorker();
            return this.commandBoxList.remove(workerCommandBox);
        }
        return false;
    }

    @Override
    public CommandBox getCommandBox() {
        return commandBox;
    }

    @Override
    public Thread getWorkerThread() {
        return currentThread;
    }

    @Override
    public boolean isRunning() {
        return working;
    }

    @Override
    public String getName() {
        return name;
    }

}
