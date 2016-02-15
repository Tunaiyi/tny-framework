package com.tny.game.actor.local.queue;

import com.tny.game.actor.ActorRef;
import com.tny.game.actor.Envelope;
import com.tny.game.actor.InternalActorRef;
import com.tny.game.actor.exception.ActorInterruptedException;
import com.tny.game.actor.local.message.DeadLetterMsg;

import java.time.Duration;
import java.util.Deque;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * 有边界双端消息队列
 * Created by Kun Yang on 16/1/19.
 */
public class BoundedDequeBasedMessageQueue extends DequeBasedMessageQueue {

    private Duration pushTimeout;

    private BlockingDeque<Envelope> deque = new LinkedBlockingDeque<>();

    @Override
    protected Deque<Envelope> queue() {
        return deque;
    }

    @Override
    public void enqueueFirst(ActorRef receiver, Envelope envelope) {
        try {
            if (pushTimeout.toMillis() >= 0) {
                if (!deque.offerFirst(envelope, pushTimeout.toMillis(), TimeUnit.MILLISECONDS)) {
                    sendToDeadLetters(receiver, envelope);
                }
            } else {
                deque.putFirst(envelope);
            }
        } catch (InterruptedException e) {
            throw new ActorInterruptedException(e);
        }
    }

    @Override
    public void enqueue(ActorRef receiver, Envelope envelope) {
        try {
            if (pushTimeout.toMillis() >= 0) {
                if (!deque.offer(envelope, pushTimeout.toMillis(), TimeUnit.MILLISECONDS)) {
                    sendToDeadLetters(receiver, envelope);
                }
            } else {
                deque.put(envelope);
            }
        } catch (InterruptedException e) {
            throw new ActorInterruptedException(e);
        }
    }

    @Override
    public Envelope dequeue() {
        return queue().poll();
    }

    private void sendToDeadLetters(ActorRef receiver, Envelope handle) {
        if (receiver instanceof InternalActorRef)
            ((InternalActorRef) receiver).getProvider().getDeadLetters().tell(
                    new DeadLetterMsg(handle.getSender(), handle.getMessage(), receiver), handle.getSender());
    }

}
