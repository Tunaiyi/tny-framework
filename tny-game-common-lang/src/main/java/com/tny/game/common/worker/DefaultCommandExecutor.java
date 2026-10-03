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

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.tny.game.common.concurrent.*;
import com.tny.game.common.utils.*;
import org.slf4j.*;

import java.util.Queue;
import java.util.concurrent.*;

/**
 * Created by Kun Yang on 2017/6/12.
 */
public class DefaultCommandExecutor implements CommandExecutor, CommandBoxWorker {

    private final ExecutorService executor;

    /** 业务池是否本执行器自建（自建者负责关闭；外部传入者不越权） */
    private final boolean ownedExecutor;

    private volatile boolean shutdown;

    private final String name;

    private static final Logger LOGGER = LoggerFactory.getLogger(LogAide.WORKER + "-" + DefaultCommandExecutor.class.getName());

    private final Queue<CommandBox<?>> commandBoxList = new ConcurrentLinkedQueue<>();

    private volatile boolean working = true;

    private ExecutorService hearbeatExecutor;

    private final long hearbeatInterval;

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
                        .setNameFormat("DefaultCommandExecutor-" + name + "-%d")
                        .build()), true);
    }

    public DefaultCommandExecutor(String name, int frequency, ExecutorService executor) {
        this(name, frequency, executor, false);
    }

    private DefaultCommandExecutor(String name, int frequency, ExecutorService executor, boolean owned) {
        this.name = name;
        this.executor = executor;
        this.ownedExecutor = owned;
        this.hearbeatInterval = Math.max(1000 / frequency, 5);
        this.start();
    }

    @Override
    public synchronized void start() {
        ExecutorService running = this.hearbeatExecutor;
        if (running != null && !running.isShutdown()) {
            throw new IllegalStateException("执行器 " + this.name + " 心跳已运行，重复启动被拒绝（防旧心跳泄漏）");
        }
        // shutdown 后显式 start 视为重启
        this.shutdown = false;
        this.working = true;
        final ExecutorService heartbeat =
                Executors.newSingleThreadExecutor(new CoreThreadFactory(this.getName() + "-SubmitThread", true));
        this.hearbeatExecutor = heartbeat;
        // 原实现循环内读 this.hearbeatExecutor 字段——重入启动覆写字段使旧循环永检新池而泄漏；
        // 改为捕获本地代际
        heartbeat.execute(() -> {
            long nextRunningTime = System.currentTimeMillis();
            while (true) {
                try {
                    if (heartbeat.isShutdown()) {
                        break;
                    }
                    this.commandBoxList.forEach(box -> {
                        if (!box.isEmpty()) {
                            box.submit();
                        }
                    });
                    nextRunningTime += this.hearbeatInterval;

                    long finishAt = System.currentTimeMillis();

                    long sleepTime = nextRunningTime - finishAt;
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
    public synchronized void shutdown() {
        stop();
        this.shutdown = true;
        ExecutorService heartbeat = this.hearbeatExecutor;
        if (heartbeat != null) {
            heartbeat.shutdownNow();
        }
        // 关闭链补全：自管业务池必须随关闭终止（外部池不越权）；原实现业务池永驻
        if (this.ownedExecutor && this.executor != null) {
            this.executor.shutdownNow();
        }
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
        for (CommandBox<?> commandBox : this.commandBoxList)
            size += commandBox.size();
        return size;
    }

    @Override
    public boolean isOnCurrentThread() {
        return false;
    }

    @Override
    public boolean register(CommandBox<?> commandBox) {
        // 关闭后注册显式失败（规格10）；绑定失败不得入列也不得虚报（规格7）
        if (this.shutdown) {
            return false;
        }
        if (!commandBox.bindWorker(new BindCommandWorker(this))) {
            return false;
        }
        this.commandBoxList.add(commandBox);
        // 重新注册即恢复暂停的执行器（stop→滞留→重注册恢复处理的通道，规格5）
        this.working = true;
        commandBox.submit();
        return true;
    }

    @Override
    public boolean unregister(CommandBox<?> commandBox) {
        if (this.commandBoxList.remove(commandBox)) {
            commandBox.unbindWorker();
            return true;
        }
        return false;
    }

    @Override
    public void wakeUp(CommandBox<?> commandBox) {
    }

    @Override
    public boolean isWorking() {
        return this.working && !this.shutdown;
    }

    @Override
    public void stop() {
        if (!this.working) {
            return;
        }
        this.working = false;
    }

    static class BindCommandWorker implements CommandBoxWorker {

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
        public boolean isShutdown() {
            // 终态如实上报：关闭后绑定盒的受理显式失败并回滚（停止≠关闭，规格受理诚实）
            return this.executor.shutdown;
        }

        @Override
        public void wakeUp(CommandBox<?> commandBox) {
            // 停止（未关闭）期间不派发：命令滞留盒内不丢失，恢复经重注册触发（规格5）
            if (!this.executor.isWorking()) {
                return;
            }
            this.executor.executor.submit(() -> doProcess(commandBox));
        }

        public void doProcess(CommandBox<?> box) {
            this.currentThread = Thread.currentThread();
            try {
                box.process();
            } finally {
                this.currentThread = null;
            }
        }

    }

}
