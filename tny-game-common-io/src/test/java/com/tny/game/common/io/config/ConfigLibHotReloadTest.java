/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tny.game.common.io.config;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ConfigLib 热更链路集成测试（config-loading「热更读取失败保留在役配置」与
 * resource-loading-robustness「文件形态变更触发热更」两规格的测试补齐）：
 * 临时目录挂 classpath + 短间隔监视设施，覆盖 onFileChange 事件触发、
 * 读取失败保留双分支、导入指向缺失文件的热更保留（首轮 tasks 2.1 的误归因面）。
 */
class ConfigLibHotReloadTest {

    @TempDir
    Path tempDir;

    private ClassLoader originalTccl;
    private URLClassLoader dirLoader;

    @BeforeEach
    void mountTempDirAsClasspath() throws Exception {
        originalTccl = Thread.currentThread().getContextClassLoader();
        dirLoader = new URLClassLoader(new URL[] { tempDir.toUri().toURL() }, originalTccl);
        Thread.currentThread().setContextClassLoader(dirLoader);
        // 顺序讲究：监视轮询线程于此处创建并继承当前 TCCL，必须先挂 classpath 再重建监视设施
        FileIOAide.useMonitorIntervalForTest(100);
    }

    @AfterEach
    void restoreClasspath() throws Exception {
        Thread.currentThread().setContextClassLoader(originalTccl);
        dirLoader.close();
    }

    private void write(String name, String content) throws IOException {
        Files.write(tempDir.resolve(name), content.getBytes(StandardCharsets.UTF_8));
    }

    private static void awaitUntil(BooleanSupplier condition, long timeoutMillis) throws InterruptedException {
        long deadline = System.currentTimeMillis() + timeoutMillis;
        while (System.currentTimeMillis() < deadline) {
            if (condition.getAsBoolean()) {
                return;
            }
            Thread.sleep(50);
        }
    }

    /** 事件触发路径（真实文件 + 短间隔监听）：变更在轮询周期内触发 reload，且按整体替换语义生效 */
    @Test
    void realFileChangeTriggersReloadThroughMonitor() throws Exception {
        String name = "hot-" + System.nanoTime() + ".properties";
        write(name, "k=v1\nonly.old=1");
        Config config = ConfigLib.getConfig(name);
        assertEquals("v1", config.getString("k"));
        assertEquals("1", config.getString("only.old"));
        Thread.sleep(500); // 观察器首个轮询周期做初快照，须待其完成后再触碰
        write(name, "k=v2");
        awaitUntil(() -> "v2".equals(config.getString("k")), 5000);
        assertEquals("v2", config.getString("k"), "文件变更回调必须在轮询周期内触发 reload");
        assertNull(config.getString("only.old"), "热更为整体替换：新表不含的键必须消失");
    }

    /** 热更读取失败保留在役配置（直接钉断言状态）+ 失败后再次成功读取照常替换 */
    @Test
    void readFailureKeepsLiveConfigThenSuccessReplaces() throws Exception {
        String name = "hotfail-" + System.nanoTime() + ".properties";
        write(name, "k=v1");
        Config config = ConfigLib.getConfig(name);
        assertEquals("v1", config.getString("k"));
        ConfigLib.ConfigFileListener listener = new ConfigLib.ConfigFileListener(name);
        Files.delete(tempDir.resolve(name));
        listener.onFileChange(tempDir.resolve(name).toFile()); // createProperties 不可读 → null
        assertEquals("v1", config.getString("k"), "读取失败必须保留在役配置（原缺陷：空表清空）");
        write(name, "k=v2");
        listener.onFileChange(tempDir.resolve(name).toFile());
        assertEquals("v2", config.getString("k"), "失败后的下一次成功读取照常替换，无永久失效");
    }

    /** 热更路径的导入失败：异常被 onFileChange 收容、在役配置保留，轮询线程不被打断 */
    @Test
    void brokenImportOnHotReloadKeepsLiveConfig() throws Exception {
        String dep = "hotdep-" + System.nanoTime() + ".properties";
        String main = "hotimp-" + System.nanoTime() + ".properties";
        write(dep, "d=dep-value");
        write(main, "tny.config.import=" + dep + "\nk=v1");
        Config config = ConfigLib.getConfig(main);
        assertEquals("v1", config.getString("k"));
        assertEquals("dep-value", config.getString("d"));
        Thread.sleep(500); // 初快照完成后才改内容，事件时序确定
        write(main, "tny.config.import=gone-" + System.nanoTime() + ".properties\nk=v2");
        ConfigLib.ConfigFileListener listener = new ConfigLib.ConfigFileListener(main);
        assertDoesNotThrow(() -> listener.onFileChange(tempDir.resolve(main).toFile()),
                "导入引发的 reload 异常不得逃逸出监听器（轮询线程被打断=后续热更全丢）");
        assertEquals("v1", config.getString("k"), "热更失败不得应用半成品表");
        assertEquals("dep-value", config.getString("d"), "旧导入合并结果同样保留");
    }

}
