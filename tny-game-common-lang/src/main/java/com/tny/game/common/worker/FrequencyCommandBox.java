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

import com.tny.game.common.worker.command.*;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FrequencyCommandBox<C extends Command, CB extends CommandBox<C>> extends AbstractWorkerCommandBox<C, CB> {

    public FrequencyCommandBox(Queue<C> queue) {
        super(queue);
    }

    public FrequencyCommandBox() {
        super(new ConcurrentLinkedQueue<>());
    }

    @Override
    protected Queue<C> acceptQueue() {
        return this.queue;
    }

    @Override
    protected void doProcess() {
        Queue<C> queue = this.acceptQueue();
        long startTime = System.currentTimeMillis();
        // poll 原子出队构造轮次（弱一致迭代器并发下可能重复扫过同一命令致重复执行——改快照物化）；
        // 轮初存量上界 + 未完成回投队尾：天然只被下一轮快照取走；runSize=完成数供自驱闭环判进展
        final int roundLimit = queue.size();
        this.runSize = 0;
        int scanned = 0;
        while (scanned < roundLimit) {
            C cmd = queue.poll();
            if (cmd == null) {
                break;
            }
            scanned++;
            executeCommand(cmd);
            if (cmd.isDone()) {
                this.runSize++;
            } else {
                queue.add(cmd);
            }
        }
        for (CommandBox<?> commandBox : boxes()) {
            commandBox.process();
            // runSize += commandBox.getProcessSize();
        }
        long finishTime = System.currentTimeMillis();
        this.runUseTime = finishTime - startTime;
    }

}
