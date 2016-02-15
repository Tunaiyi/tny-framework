package com.tny.game.actor.local.queue;

import com.tny.game.actor.ActorRef;
import com.tny.game.actor.SystemMessage;
import com.tny.game.actor.SystemMessageQueue;
import com.tny.game.actor.local.ActorCell;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 默认的系统消息队列
 * Created by Kun Yang on 16/1/19.
 */
public class DefaultSystemMessageQueue implements SystemMessageQueue {

    private ActorCell actor;

    private volatile Queue<SystemMessage> messageQueue = new ConcurrentLinkedQueue<>();

    public DefaultSystemMessageQueue(ActorCell actorCell) {
        this.actor = actorCell;
    }

    @Override
    public void systemEnqueue(ActorRef receiver, SystemMessage message) {
        Queue<SystemMessage> messageQueue = this.messageQueue;
        if (messageQueue == null) {
            if (actor != null) {
                actor.getPostman().getPostboxes().getDeadLetterPostbox().systemEnqueue(receiver, message);
            }
        } else {
            messageQueue.add(message);
        }
    }

    @Override
    public boolean hasSystemMessages() {
        return messageQueue.isEmpty();
    }

    @Override
    public Optional<Queue<SystemMessage>> systemDrain() {
        Queue<SystemMessage> messages = this.messageQueue;
        if (messages == null) {
            return Optional.empty();
        } else {
            this.messageQueue = new ConcurrentLinkedQueue<>();
            return Optional.of(messages);
        }
    }

    @Override
    public void dispose() {
        this.messageQueue = null;
    }
}
