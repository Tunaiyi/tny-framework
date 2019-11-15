package com.tny.game.net.transport;

import com.google.common.collect.ImmutableList;
import com.tny.game.net.message.*;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.*;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2019-03-27 15:30
 */
public class MessageQueue<UID> {

    /* 发送缓存 */
    private CircularFifoQueue<Message<UID>> sentMessageQueue = null;

    /* 锁 */
    private StampedLock sentMessageLock;

    public MessageQueue(int cacheSentMessageSize) {
        if (cacheSentMessageSize > 0) {
            this.sentMessageQueue = new CircularFifoQueue<>(cacheSentMessageSize);
            sentMessageLock = new StampedLock();
        }
    }

    public List<Message<UID>> getMessages(Predicate<Message<UID>> filter) {
        if (this.sentMessageQueue == null)
            return ImmutableList.of();
        StampedLock lock = this.sentMessageLock;
        long stamp = lock.readLock();
        try {
            return this.sentMessageQueue.stream()
                                        .filter(filter)
                                        .collect(Collectors.toList());
        } finally {
            lock.unlockRead(stamp);
        }
    }


    public List<Message<UID>> getAllMessages() {
        if (this.sentMessageQueue == null)
            return ImmutableList.of();
        StampedLock lock = this.sentMessageLock;
        long stamp = lock.readLock();
        try {
            return new ArrayList<>(this.sentMessageQueue);
        } finally {
            lock.unlockRead(stamp);
        }
    }

    public void addMessage(Message<UID> message) {
        if (this.sentMessageQueue != null) {
            StampedLock lock = this.sentMessageLock;
            long stamp = lock.writeLock();
            try {
                this.sentMessageQueue.add(message);
            } finally {
                lock.unlockWrite(stamp);
            }
        }
    }

}
