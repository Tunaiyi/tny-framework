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
package com.tny.game.common.io.config;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FileMonitor 监听注册/注销契约（回归：removeFileListener 原实现误调 addListener，
 * 注销变追加、监听器只增不减）。轮询间隔取小值以压缩用例时长，断言用有界轮询避免 sleep 抖动。
 */
class FileMonitorTest {

    /** 有界轮询直到条件成立或超时 */
    private static void awaitUntil(BooleanSupplier condition, long timeoutMillis) throws InterruptedException {
        long deadline = System.currentTimeMillis() + timeoutMillis;
        while (System.currentTimeMillis() < deadline) {
            if (condition.getAsBoolean()) {
                return;
            }
            Thread.sleep(50);
        }
    }

    private interface BooleanSupplier {
        boolean getAsBoolean();
    }

    @TempDir
    Path tempDir;

    @Test
    void addRemoveListenerRoundTrip() throws Exception {
        FileMonitor monitor = new FileMonitor(100);
        try {
            Path watched = tempDir.resolve("watched.properties");
            Files.write(watched, "k=1".getBytes(StandardCharsets.UTF_8));
            String absolute = watched.toString().replace("\\", "/");

            AtomicInteger changes = new AtomicInteger();
            FileAlterationListenerAdaptor listener = new FileAlterationListenerAdaptor() {
                @Override
                public void onFileCreate(File file) {
                    changes.incrementAndGet();
                }

                @Override
                public void onFileChange(File file) {
                    changes.incrementAndGet();
                }
            };
            monitor.addFileListener(absolute, listener);

            // 触碰文件（修改时间 + 内容变化），等待回调
            Thread.sleep(300);
            Files.write(watched, "k=2".getBytes(StandardCharsets.UTF_8));
            awaitUntil(() -> changes.get() >= 1, 5000);
            assertTrue(changes.get() >= 1, "注册后文件变更应触发回调");

            // 等待当前轮询派发批次的尾巴结束（间隔 100ms）后再取计数与注销：
            // 避免冷 JVM 负载下在途投递仍持旧监听快照，出现"注销后回调"的时序假阳性
            Thread.sleep(400);
            // 注销后再次变更不得计数（回归点：原实现 remove 变 add）
            int before = changes.get();
            monitor.removeFileListener(absolute, listener);
            Thread.sleep(300);
            Files.write(watched, "k=3".getBytes(StandardCharsets.UTF_8));
            Thread.sleep(1000);
            assertEquals(before, changes.get(), "removeFileListener 后不得再收到回调");
        } finally {
            monitor.stop();
        }
    }

    /** 无目录分隔的路径不得抛 StringIndexOutOfBoundsException（原实现 substring(0, -1)） */
    @Test
    void relativeFileNameDoesNotCrashObserverCreation() {
        FileMonitor monitor = new FileMonitor(100);
        try {
            FileAlterationListenerAdaptor listener = new FileAlterationListenerAdaptor() {
            };
            assertDoesNotThrow(() -> monitor.addFileListener("plain-file-name.txt", listener));
            assertDoesNotThrow(() -> monitor.removeFileListener("plain-file-name.txt", listener));
        } finally {
            monitor.stop();
        }
    }

    /** 空文件内容不得进入 null-key 崩溃路径（配合 PropertiesConfig 的 null 值受控异常） */
    @Test
    void emptyPropertiesConfigMapWorks() {
        Config config = ConfigLib.newConfig(new java.util.HashMap<>());
        assertNull(config.getString("anything"));
        assertEquals(0, config.keySet().size());
    }

}
