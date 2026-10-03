/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
    private volatile boolean working = true;

    /**
     * 当前线程
     */
    protected volatile Thread currentThread;

    private final Queue<CommandBox<?>> commandBoxQueue = new ConcurrentLinkedQueue<>();

    private volatile ExecutorService executor;

    /** 终态标志：shutdown 后禁止注册/启动 */
    private volatile boolean shutdown;

    private long nextRunningTime;

    private long totalRunningTime;

    private long totalSleepTime;

    private long totalRunSize;

    private long sleepTime;

    private long runningTime;

    private int runSize;

    private int continueTime;

    private final CommandBoxWorker worker = new CommandBoxWorker() {

        @Override
        public boolean isOnCurrentThread() {
            return FrequencyCommandExecutor.this.currentThread == Thread.currentThread();
        }

        @Override
        public boolean isWorking() {
            return FrequencyCommandExecutor.this.isWorking();
        }

        @Override
        public boolean isShutdown() {
            // 终态如实上报：关闭后绑定盒的受理显式失败并回滚（停止≠关闭，规格受理诚实）
            return FrequencyCommandExecutor.this.shutdown;
        }

        @Override
        public boolean register(CommandBox<?> commandBox) {
            return FrequencyCommandExecutor.this.register(commandBox);
        }

        @Override
        public boolean unregister(CommandBox<?> commandBox) {
            // 原实现误写为 register：注销变注册
            return FrequencyCommandExecutor.this.unregister(commandBox);
        }

        @Override
        public void wakeUp(CommandBox<?> commandBox) {
        }

    };

    public FrequencyCommandExecutor(String name) {
        this.name = name;
    }

    @Override
    public void stop() {
        if (!this.working) {
            return;
        }
        this.working = false;
    }

    @Override
    public synchronized void start() {
        ExecutorService running = this.executor;
        if (running != null && !running.isShutdown()) {
            throw new IllegalStateException("执行器 " + this.name + " 已启动，重复启动被拒绝（防旧心跳泄漏）");
        }
        // shutdown 后显式 start 视为重启（清终态标志）；关闭后"注册"仍显式失败（规格10）
        this.shutdown = false;
        this.working = true;
        final ExecutorService heartbeat =
                Executors.newSingleThreadExecutor(new CoreThreadFactory(this.name, true));
        this.executor = heartbeat;
        // 原实现循环内读 this.executor 字段——重入启动覆写字段使旧心跳永检新池而泄漏；改捕获本地代际
        heartbeat.execute(() -> {
            this.nextRunningTime = System.currentTimeMillis();
            this.currentThread = Thread.currentThread();
            while (true) {
                try {
                    if (heartbeat.isShutdown()) {
                        break;
                    }
                    long currentTime = System.currentTimeMillis();
                    int currentRunSize = 0;
                    int currentContinueTime = 0;
                    while (currentTime >= this.nextRunningTime) {
                        for (CommandBox<?> box : this.commandBoxQueue) {
                            // 心跳必须真实驱动处理（原实现调用空 wakeUp，注册盒永不被处理）；
                            // 逐盒捕获，单盒异常不连坐其余盒与心跳线程
                            try {
                                box.process();
                                if (box instanceof WorkerCommandBox<?, ?> workerBox) {
                                    currentRunSize += workerBox.getProcessSize();
                                }
                            } catch (Throwable e) {
                                LOGGER.warn("FrequencyWorker box process exception", e);
                            }
                        }
                        this.nextRunningTime += 100L;
                        currentContinueTime++;
                        currentTime = System.currentTimeMillis();
                    }
                    if (this.commandBoxQueue.isEmpty() && !this.working) {
                        heartbeat.shutdown();
                        return;
                    }

                    this.continueTime = currentContinueTime;
                    this.runSize = currentRunSize;
                    this.totalRunSize += this.runSize;
                    if (this.totalRunSize < 0) {
                        this.totalRunSize = 0;
                    }

                    long stopTime = System.currentTimeMillis();
                    this.runningTime = stopTime - currentTime;

                    this.sleepTime = this.nextRunningTime - stopTime;
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
    public synchronized void shutdown() {
        stop();
        this.shutdown = true;
        ExecutorService heartbeat = this.executor;
        if (heartbeat != null) {
            heartbeat.shutdownNow();
        }
    }

    @Override
    public String toString() {
        long totalSleepTime = this.totalSleepTime;
        long totalRunningTime = this.totalRunningTime;
        long sleepTime = this.sleepTime;
        long runningTime = this.runningTime;
        int continueTime = this.continueTime;
        return this.getName() +
               " #任务数量: " +
               size() +
               " #附加任务箱数量: " +
               this.commandBoxQueue.size() +
               " #总运行数量" +
               this.totalRunSize +
               " #最近运行数量" +
               this.runSize +
               " #最近连续次数: " +
               continueTime +
               " #最近休眠时间: " +
               sleepTime +
               " #最近运行时间" +
               runningTime +
               " #最近休眠比率: " +
               (double) sleepTime / (double) (sleepTime + runningTime) +
               " #休眠总时间: " +
               totalSleepTime +
               " #运行总时间: " +
               totalRunningTime +
               " #总休眠比率: " +
               (double) totalSleepTime / (double) (totalSleepTime + totalRunningTime);
    }

    @Override
    public int size() {
        int size = 0;
        for (CommandBox<?> commandBox : this.commandBoxQueue)
            size += commandBox.size();
        return size;
    }

    @Override
    public boolean register(CommandBox<?> commandBox) {
        // 关闭/停止后注册显式失败（不得虚报成功使命令静默滞留，规格10）
        if (this.shutdown || !this.working) {
            return false;
        }
        if (commandBox instanceof WorkerCommandBox<?, ?> workerCommandBox) {
            if (workerCommandBox.bindWorker(this.worker)) {
                this.commandBoxQueue.add(workerCommandBox);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean unregister(CommandBox<?> commandBox) {
        if (commandBox instanceof WorkerCommandBox<?, ?> workerCommandBox) {
            if (this.commandBoxQueue.remove(workerCommandBox)) {
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
