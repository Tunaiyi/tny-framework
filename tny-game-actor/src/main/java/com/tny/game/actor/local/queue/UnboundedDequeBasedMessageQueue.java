package com.tny.game.actor.local.queue;

import com.tny.game.actor.ActorRef;
import com.tny.game.actor.Envelope;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * 无边界双端消息队列
 * Created by Kun Yang on 16/1/19.
 */
public class UnboundedDequeBasedMessageQueue extends DequeBasedMessageQueue {

    private Deque<Envelope> deque = new ConcurrentLinkedDeque<>();

    @Override
    protected Deque<Envelope> queue() {
        return deque;
    }

    @Override
    public void enqueueFirst(ActorRef receiver, Envelope envelope) {
        deque.addFirst(envelope);
    }

    @Override
    public void enqueue(ActorRef receiver, Envelope envelope) {
        queue().add(envelope);
    }

    @Override
    public Envelope dequeue() {
        return queue().poll();
    }

}
