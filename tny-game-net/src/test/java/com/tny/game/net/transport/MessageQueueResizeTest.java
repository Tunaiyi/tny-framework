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

import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.message.*;
import org.junit.jupiter.api.*;

import java.util.concurrent.atomic.*;

import static com.tny.game.net.command.dispatcher.RpcContextFixture.message;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 发送缓存动态调整行为（session-resend 规格 · 发送缓存的动态启用与禁用 / 环形保留窗口）。
 */
class MessageQueueResizeTest {

    /** 禁用 → 启用：首次调整不得抛异常（当前实现 addAll(null) NPE，应红） */
    @Test
    void enableFromDisabledState() {
        MessageQueue queue = new MessageQueue(0);
        queue.addMessage(message(1L, System.currentTimeMillis())); // 禁用期不保留
        queue.resize(3);
        queue.addMessage(message(2L, System.currentTimeMillis()));
        queue.addMessage(message(3L, System.currentTimeMillis()));
        assertEquals(2, queue.getAllMessages().size(), "启用后新发送应被保留");
    }

    /** 启用 → 禁用：置 0 应停止保留并释放（当前实现 CircularFifoQueue(0) 容量异常，应红） */
    @Test
    void disableReleasesRetainedMessages() {
        MessageQueue queue = new MessageQueue(2);
        queue.addMessage(message(1L, System.currentTimeMillis()));
        queue.addMessage(message(2L, System.currentTimeMillis()));
        queue.resize(0);
        assertEquals(0, queue.getAllMessages().size(), "禁用后已保留消息应释放");
        queue.addMessage(message(3L, System.currentTimeMillis()));
        assertEquals(0, queue.getMessages(m -> true).size(), "禁用期不得继续保留");
    }

    /** 缩容 M → N：保留最近 N 条，不抛异常（当前实现碰巧成立，防回归绿基线） */
    @Test
    void shrinkKeepsMostRecent() {
        MessageQueue queue = new MessageQueue(5);
        for (int i = 1; i <= 5; i++) {
            queue.addMessage(message(i, System.currentTimeMillis()));
        }
        queue.resize(3);
        assertEquals(3, queue.getAllMessages().size(), "缩容后保留不超过新容量");
    }

    /** 环形挤出：超过容量的旧消息不可见（当前实现已正确，防回归） */
    @Test
    void ringEvictionBoundsMemory() {
        MessageQueue queue = new MessageQueue(2);
        for (int i = 1; i <= 4; i++) {
            queue.addMessage(message(i, System.currentTimeMillis()));
        }
        assertEquals(2, queue.getAllMessages().size());
    }

    /** 并发 smoke：resize × add/get 交错不得抛异常（当前 resize NPE 连锁，应红或偶发红） */
    @Test
    void concurrentResizeAndAccessMustNotThrow() throws Exception {
        MessageQueue queue = new MessageQueue(4);
        AtomicReference<Throwable> failure = new AtomicReference<>();
        Thread resizing = new Thread(() -> {
            try {
                for (int i = 0; i < 100; i++) {
                    queue.resize(i % 2 == 0 ? 0 : 4);
                }
            } catch (Throwable t) {
                failure.compareAndSet(null, t);
            }
        }, "resize-thread");
        Thread accessing = new Thread(() -> {
            try {
                for (int i = 0; i < 200; i++) {
                    queue.addMessage(message(i, System.currentTimeMillis()));
                    queue.getMessages(m -> true);
                    queue.getAllMessages();
                }
            } catch (Throwable t) {
                failure.compareAndSet(null, t);
            }
        }, "access-thread");
        resizing.start();
        accessing.start();
        resizing.join(10_000);
        accessing.join(10_000);
        assertNull(failure.get(), () -> "并发访问抛出异常: " + failure.get());
    }

}
