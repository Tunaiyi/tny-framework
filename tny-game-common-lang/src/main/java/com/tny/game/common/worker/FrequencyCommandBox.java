/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
    public void submit() {
    }

    @Override
    protected void doProcess() {
        Queue<C> queue = this.acceptQueue();
        long startTime = System.currentTimeMillis();
        int currentSize = queue.size();
        this.runSize = 0;
        for (C cmd : queue) {
            currentSize++;
            if (this.runSize > currentSize) {
                break;
            }
            executeCommand(cmd);
            this.runSize++;
            if (cmd.isDone()) {
                queue.remove(cmd);
            }
        }
        for (CommandBox<?> commandBox : boxes()) {
            commandBox.process();
            // runSize += commandBox.getProcessSize();
        }
        long finishTime = System.currentTimeMillis();
        this.runUseTime = finishTime - startTime;
    }

    @Override
    public void wakeUp(CommandBox<?> commandBox) {
    }

}
