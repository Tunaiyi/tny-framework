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
