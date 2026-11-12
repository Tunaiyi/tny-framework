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
            // 原第三行把翻转结果覆写回原队列——切换恒为无操作
            Queue<T> accQueue = this.queue;
            this.queue = accQueue != this.toQueue ? this.toQueue : this.fromQueue;
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
