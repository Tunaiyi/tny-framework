/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

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
 * @author Kun Yang
 * @date 2019-03-27 15:30
 */
public class MessageQueue {

    /* 发送缓存 */
    private CircularFifoQueue<Message> sentMessageQueue = null;

    /* 锁 */
    private StampedLock sentMessageLock;

    public MessageQueue(int cacheSentMessageSize) {
        if (cacheSentMessageSize > 0) {
            this.sentMessageQueue = new CircularFifoQueue<>(cacheSentMessageSize);
            this.sentMessageLock = new StampedLock();
        }
    }

    public List<Message> getMessages(Predicate<Message> filter) {
        if (this.sentMessageQueue == null) {
            return ImmutableList.of();
        }
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

    public List<Message> getAllMessages() {
        if (this.sentMessageQueue == null) {
            return ImmutableList.of();
        }
        StampedLock lock = this.sentMessageLock;
        long stamp = lock.readLock();
        try {
            return new ArrayList<>(this.sentMessageQueue);
        } finally {
            lock.unlockRead(stamp);
        }
    }

    public void addMessage(Message message) {
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
