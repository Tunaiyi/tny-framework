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
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2019-03-27 15:30
 */
public class MessageQueue {

    /* 发送缓存（null=禁用；volatile 保证锁外 null 检查可见 resize 后的新引用）。
       以最低接口 Queue 声明：容量挤出是 CircularFifoQueue 实现特性，契约固化于 session-resend 规格 */
    private volatile Queue<Message> sentMessageQueue = null;

    /* 锁：ReentrantLock——可重入、短临界区，且虚拟线程兼容（JDK 21 上 synchronized
       竞争阻塞会 pin 载体线程，JEP 491 前不可用于热路径；模式卷选型行）。
       注意：本队列在玩家会话近单生产者，但服务器间会话为多生产者高频写入，锁竞争真实可能。 */
    private final ReentrantLock sentMessageLock = new ReentrantLock();

    private int messageSize;

    public MessageQueue(int messageSize) {
        this.messageSize = messageSize;
        if (messageSize > 0) {
            this.sentMessageQueue = new CircularFifoQueue<>(messageSize);
        }
    }

    public void resize(int messageSize) {
        if (this.messageSize == messageSize) {
            return;
        }
        sentMessageLock.lock();
        try {
            this.messageSize = messageSize;
            if (messageSize <= 0) {
                // 0=禁用并释放（与构造语义一致，session-resend 规格 D1）
                this.sentMessageQueue = null;
                return;
            }
            var old = this.sentMessageQueue;
            Queue<Message> replacement = new CircularFifoQueue<>(messageSize);
            if (old != null) {
                replacement.addAll(old); // 超容量自动挤出最旧，保留最近 N（D2）
            }
            this.sentMessageQueue = replacement;
        } finally {
            sentMessageLock.unlock();
        }
    }

    public List<Message> getMessages(Predicate<Message> filter) {
        sentMessageLock.lock();
        try {
            Queue<Message> queue = this.sentMessageQueue;
            if (queue == null) {
                return ImmutableList.of();
            }
            return queue.stream()
                    .filter(filter)
                    .collect(Collectors.toList());
        } finally {
            sentMessageLock.unlock();
        }
    }

    public List<Message> getAllMessages() {
        sentMessageLock.lock();
        try {
            Queue<Message> queue = this.sentMessageQueue;
            if (queue == null) {
                return ImmutableList.of();
            }
            return new ArrayList<>(queue);
        } finally {
            sentMessageLock.unlock();
        }
    }

    public void addMessage(Message message) {
        if (this.sentMessageQueue != null) { // volatile 读短路禁用态：高频发送路径零锁开销
            sentMessageLock.lock();
            try {
                Queue<Message> queue = this.sentMessageQueue;
                if (queue != null) { // 锁内重读：检查与使用之间引用可能已被 resize 置换（D3）
                    queue.add(message);
                }
            } finally {
                sentMessageLock.unlock();
            }
        }
    }

}
