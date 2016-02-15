package com.tny.game.actor.local.queue;

import com.tny.game.actor.ActorRef;
import com.tny.game.actor.Envelope;
import com.tny.game.actor.MessageQueue;

import java.util.Deque;

/**
 * 基于双端队列的抽象消息队列
 * Created by Kun Yang on 16/1/19.
 */
public abstract class DequeBasedMessageQueue implements MessageQueue {

    protected abstract Deque<Envelope> queue();

    @Override
    public int size() {
        return queue().size();
    }

    @Override
    public boolean hasMessages() {
        return !queue().isEmpty();
    }

    @Override
    public void cleanUp(ActorRef owner, MessageQueue deadLetters) {
        if (hasMessages()) {
            Envelope envelope = dequeue();
            while (envelope != null) {
                deadLetters.enqueue(owner, envelope);
                envelope = dequeue();
            }
        }
    }
}
