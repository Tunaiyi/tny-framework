package com.tny.game.net.transport;

import com.google.common.collect.ImmutableList;
import com.tny.game.net.message.Message;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-11 19:44
 */
public class MessageCachedQueue<UID> {

    /* 缓存最大数量 */
    private int maxCacheMessageSize;

    /* 发送缓存 */
    private Deque<Message<UID>> sentMessageQueue = null;

    /* 缓存队列锁 */
    private ReadWriteLock cacheLock;

    public MessageCachedQueue(int maxCacheMessageSize) {
        this.maxCacheMessageSize = maxCacheMessageSize;
        if (maxCacheMessageSize > 0) {
            sentMessageQueue = new ConcurrentLinkedDeque<>();
            cacheLock = new ReentrantReadWriteLock();
        }
    }

    public List<Message<UID>> getSendMessages(long from, long to) {
        if (this.sentMessageQueue == null)
            return ImmutableList.of();
        Lock lock = this.cacheLock.readLock();
        lock.lock();
        try {
            List<Message<UID>> messages = new ArrayList<>();
            boolean start = from < 0;
            for (Message<UID> message : this.sentMessageQueue) {
                if (!start) {
                    if (message.getId() == from)
                        start = true;
                    else
                        continue;
                }
                messages.add(message);
                if (message.getId() == to)
                    break;
            }
            return messages;
        } finally {
            lock.unlock();
        }
    }

    public Message<UID> getSendMessage(long messageId) {
        if (this.sentMessageQueue == null)
            return null;
        Lock lock = this.cacheLock.readLock();
        lock.lock();
        try {
            return this.sentMessageQueue.stream()
                    .filter(message -> message.getHeader().getId() == messageId)
                    .findFirst()
                    .orElse(null);
        } finally {
            lock.unlock();
        }
    }

    public void addSendMessage(Message<UID> message) {
        if (this.sentMessageQueue != null) {
            this.sentMessageQueue.add(message);
            if (this.sentMessageQueue.size() <= maxCacheMessageSize)
                return;
            Lock lock = this.cacheLock.writeLock();
            if (lock.tryLock()) {
                try {
                    while (this.sentMessageQueue.size() > maxCacheMessageSize)
                        this.sentMessageQueue.poll();
                } finally {
                    lock.unlock();
                }
            }
        }
    }

}
