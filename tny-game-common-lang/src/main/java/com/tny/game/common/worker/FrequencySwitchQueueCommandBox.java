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

import com.tny.game.common.utils.*;
import com.tny.game.common.worker.command.*;
import org.slf4j.*;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.*;

public class FrequencySwitchQueueCommandBox<C extends Command, CB extends CommandBox<C>> extends AbstractWorkerCommandBox<C, CB> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(LogAide.WORKER);

    private final Queue<C> fromQueue;

    private final Queue<C> toQueue;

    private final Lock lock = new ReentrantLock();

    public FrequencySwitchQueueCommandBox(Queue<C> fromQueue, Queue<C> toQueue) {
        super(new ConcurrentLinkedQueue<>());
        this.fromQueue = fromQueue;
        this.toQueue = toQueue;
        this.queue = fromQueue;
    }

    public FrequencySwitchQueueCommandBox() {
        this(new ConcurrentLinkedQueue<>(), new ConcurrentLinkedQueue<>());
    }

    @Override
    protected Queue<C> acceptQueue() {
        while (true) {
            if (this.lock.tryLock()) {
                try {
                    // 原实现把翻转结果覆写回原队列——切换恒为无操作，
                    // 未完成命令回投同轮即被再次取到
                    Queue<C> accQueue = this.queue;
                    this.queue = accQueue != this.toQueue ? this.toQueue : this.fromQueue;
                    return accQueue;
                } finally {
                    this.lock.unlock();
                }
            } else {
                Thread.yield();
            }
        }
    }

    @Override
    public void clear() {
        this.queue.clear();
        this.toQueue.clear();
        this.fromQueue.clear();
    }

    @Override
    public boolean isEmpty() {
        return this.toQueue.isEmpty() && this.fromQueue.isEmpty();
    }

    @Override
    public int size() {
        return this.toQueue.size() + this.fromQueue.size();
    }

    @Override
    protected void doProcess() {
        Queue<C> currentRunQueue = this.acceptQueue();
        long startTime = System.currentTimeMillis();
        this.runSize = 0;
        C cmd = currentRunQueue.poll();
        while (cmd != null) {
            executeCommand(cmd);
            this.runSize++;
            if (!cmd.isDone()) {
                this.queue.add(cmd);
            }
            cmd = currentRunQueue.poll();
        }
        for (CommandBox<?> commandBox : boxes()) {
            commandBox.process();
            // commandBox.process();
            // this.worker.submit(commandBox);
            // runSize += commandBox.getProcessSize();
        }
        long finishTime = System.currentTimeMillis();
        this.runUseTime = finishTime - startTime;
    }

}
