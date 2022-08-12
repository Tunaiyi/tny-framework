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

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SwitchConcurrentQueue<T> {

    protected volatile Queue<T> queue;

    protected Queue<T> fromQueue;

    protected Queue<T> toQueue;

    public SwitchConcurrentQueue(Queue<T> fromQueue, Queue<T> toQueue) {
        super();
        this.fromQueue = fromQueue;
        this.toQueue = toQueue;
        this.queue = fromQueue;
    }

    public SwitchConcurrentQueue() {
        this(new ConcurrentLinkedQueue<T>(), new ConcurrentLinkedQueue<T>());
    }

    public Queue<T> acceptQueue() {
        synchronized (this) {
            Queue<T> accQueue = this.queue;
            this.queue = accQueue != this.toQueue ? this.toQueue : this.fromQueue;
            this.queue = accQueue;
            return accQueue;
        }
    }

    public boolean isEmpty() {
        return toQueue.isEmpty() && fromQueue.isEmpty();
    }

    public int size() {
        return toQueue.size() + fromQueue.size();
    }

    public void clear() {
        queue.clear();
        toQueue.clear();
        fromQueue.clear();
    }

}
