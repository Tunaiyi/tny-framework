package com.tny.game.common.worker;

import com.tny.game.common.concurrent.*;
import com.tny.game.common.utils.*;
import org.slf4j.*;

import java.util.Queue;
import java.util.concurrent.*;

public class FrequencyCommandExecutor implements CommandExecutor {

    protected static final Logger LOGGER = LoggerFactory.getLogger(LogAide.WORKER);

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
            return FrequencyCommandExecutor.this.currentThread == Thread.currentThread();
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

        @Override
        public boolean execute(CommandBox commandBox) {
            return true;
        }

    };

    public FrequencyCommandExecutor(String name) {
        this.name = name;
    }

    @Override
    public void stop() {
        if (!this.working)
            return;
        this.working = false;
        this.stopTime = System.currentTimeMillis();
    }


    @Override
    public void start() {
        this.executor = Executors.newSingleThreadExecutor(new CoreThreadFactory(this.name, true));
        this.executor.execute(() -> {
            this.nextRunningTime = System.currentTimeMillis();
            this.currentThread = Thread.currentThread();
            while (true) {
                try {
                    if (this.executor.isShutdown())
                        break;
                    long currentTime = System.currentTimeMillis();
                    int currentRunSize = 0;
                    int currentContinueTime = 0;
                    while (currentTime >= this.nextRunningTime) {
                        for (CommandBox box : this.commandBoxList) {
                            this.worker.execute(box);
                            // box.getProcessUseTime();
                            // currentRunSize += box.getProcessSize();
                        }
                        this.nextRunningTime += 100L;
                        currentContinueTime++;
                        currentTime = System.currentTimeMillis();
                    }
                    if (this.commandBoxList.isEmpty() && !this.working)
                        return;

                    this.continueTime = currentContinueTime;
                    this.runSize = currentRunSize;
                    this.totalRunSize += this.runSize;
                    if (this.totalRunSize < 0)
                        this.totalRunSize = 0;

                    long stopTime1 = System.currentTimeMillis();
                    this.runningTime = stopTime1 - currentTime;

                    this.sleepTime = this.nextRunningTime - stopTime1;
                    this.sleepTime = this.sleepTime < 0 ? 0 : this.sleepTime;

                    this.totalRunningTime += this.runningTime;
                    this.totalSleepTime += this.sleepTime;
                    if (this.sleepTime > 0) {
                        Thread.sleep(this.sleepTime);
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
        this.executor.shutdown();
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
                .append(this.totalRunSize)
                .append(" #最近运行数量")
                .append(this.runSize)
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
        for (CommandBox commandBox : this.commandBoxList)
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
        return this.name;
    }

}
