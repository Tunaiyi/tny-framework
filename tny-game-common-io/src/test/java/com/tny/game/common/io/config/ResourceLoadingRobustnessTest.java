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

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.jar.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * resource-loading-robustness 契约（fix-common-dormant-defects 组8）：
 * 转义/空格路径的类路径文件定位真实可用；系统属性装载命名空间守卫与空流受控；
 * 导入环 fail-fast（原无限互递归）；并发首载单实例；构建器产物与源双向隔离；
 * 子配置查找作用域限定（首轮遗留 D12 入册）。
 */
class ResourceLoadingRobustnessTest {

    // ---- 资源定位 ----

    /** 空格资源名经 URL→File 转换后仍可定位（原 %20 路径 exists()==false） */
    @Test
    void spacedResourceLocatesToExistingFile() {
        File file = FileIOAide.loadFile("io spaced resource.properties");
        assertNotNull(file, "含空格资源必须可定位");
        assertTrue(file.exists(), "URL 转义未还原导致定位到不存在的路径（原缺陷）");
    }

    /** 内容读取通道对空格资源正常 */
    @Test
    void spacedResourceContentReadable() throws Exception {
        try (InputStream in = FileIOAide.openInputStream("io spaced resource.properties")) {
            Properties props = new Properties();
            props.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            assertEquals("spaced-value", props.getProperty("spaced.key"));
        }
    }

    // ---- 系统属性守卫 ----

    /** 关键 JVM 属性必须被拒绝，业务键正常装载；越界键可观测 */
    @Test
    void systemPropertiesNamespaceGuarded() throws Exception {
        String originalHome = System.getProperty("user.home");
        String originalCp = System.getProperty("java.class.path");
        new SystemPropertiesLoader("io-system-guard.properties");
        assertEquals("guarded-value", System.getProperty("guard.app.key"), "业务命名空间键应装载");
        assertSame(originalHome, System.getProperty("user.home"), "JVM 关键属性不得被文件覆盖");
        assertSame(originalCp, System.getProperty("java.class.path"), "JVM 关键属性不得被文件覆盖");
        List<String> rejected = SystemPropertiesLoader.lastRejectedKeys();
        assertTrue(rejected.contains("user.home") && rejected.contains("java.class.path"),
                "被拒键必须可查询（静默丢弃不可观测）: " + rejected);
        System.clearProperty("guard.app.key");
    }

    /** 不存在的装载文件：受控异常（不得裸 NPE 掩盖真因） */
    @Test
    void missingSystemPropertiesFileFailsControlled() {
        assertThrows(IOException.class, () -> new SystemPropertiesLoader("no-such-props-file-xyz.properties"));
    }

    // ---- 导入环与首载 ----

    /** 互斥环导入 fail-fast 含路径链（原无限互递归栈溢出） */
    @Test
    void cyclicImportFailsFastWithChain() {
        Throwable thrown = assertThrows(Throwable.class,
                () -> ConfigLib.getExistConfig("io-cyclic-a.properties"));
        Throwable root = thrown;
        while (root.getCause() != null) {
            root = root.getCause();
        }
        assertInstanceOf(IllegalArgumentException.class, root,
                "环导入必须是显式参数异常（栈溢出泄漏=原缺陷形态）");
        assertTrue(String.valueOf(root.getMessage()).contains("io-cyclic-a")
                        && String.valueOf(root.getMessage()).contains("io-cyclic-b"),
                "异常信息须含环路径链: " + root.getMessage());
    }

    /** 并发首载同一文件：全部调用者拿到同一实例（监听器不双挂） */
    @Test
    void concurrentFirstLoadYieldsSingleton() throws Exception {
        int threads = 8;
        CyclicBarrier barrier = new CyclicBarrier(threads);
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        try {
            List<Future<Config>> futures = new ArrayList<>();
            for (int t = 0; t < threads; t++) {
                futures.add(pool.submit(() -> {
                    barrier.await(5, TimeUnit.SECONDS);
                    return ConfigLib.getConfig("io-import-main.properties");
                }));
            }
            Set<Config> distinct = new HashSet<>();
            for (Future<Config> f : futures) {
                distinct.add(f.get(10, TimeUnit.SECONDS));
            }
            assertEquals(1, distinct.size(), "并发首载产生多实例（监听器双挂、reload 双跑）");
        } finally {
            pool.shutdownNow();
        }
    }

    /**
     * 导入同名键优先级如实钉桩（现契约）：后声明的导入来源覆盖同名键（含主文件自身值）；
     * 重复来源去重保首现位置（ImmutableSet 语义，后现重复不再生效）。
     */
    @Test
    void importPriorityLaterDeclarationOverridesAndDedupKeepsFirst() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put(PropertiesConfig.IMPORT_KEY,
                "io-import-ordered-a.properties, io-import-ordered-b.properties, io-import-ordered-a.properties");
        m.put("shared.key", "main");
        m.put("own.key", "own");
        Config config = ConfigLib.newConfig(m);
        assertEquals("own", config.getString("own.key"), "主文件独有键必须保留");
        assertEquals("1", config.getString("a.only"), "各来源独有键不得被吞没");
        assertEquals("1", config.getString("b.only"));
        assertEquals("b", config.getString("shared.key"),
                "同名键裁定：后声明导入来源胜出（b 覆盖 a 与主文件自身值；末尾重复声明的 a 因去重不再生效）");
        // 同一输入重复装载结果一致（不随装载次序随机化）
        Config again = ConfigLib.newConfig(m);
        assertEquals("b", again.getString("shared.key"));
    }

    // ---- 构建器隔离 ----

    /** build 后 builder 继续写入不得影响已产出配置 */
    @Test
    void builderOutputIsolatedFromLaterMutation() {
        ConfigBuilder builder = ConfigBuilder.newBuilder();
        builder.put("stable", "1");
        Config built = builder.build();
        builder.put("late", "2");
        assertEquals("1", built.getString("stable"));
        assertNull(built.getString("late"), "构建产物被后续 builder 写入穿透（活引用共享）");
    }

    // ---- 交付形态（归档 vs 缺失）与转义监听登记 ----

    /**
     * 归档形态（REAL_GAP 修复）：loadFile 定位不了文件 ≠ 资源缺失——getConfig 必须经流通道
     * 读真实内容，不得以假成功交付空表；resourceExists 为存在性判定提供确定依据。
     */
    @Test
    void archivedResourceDeliversRealContent() throws Exception {
        String name = "jarprobed-" + System.nanoTime() + ".properties";
        Path jar = Files.createTempFile("cfg-probe", ".jar");
        try (JarOutputStream out = new JarOutputStream(Files.newOutputStream(jar))) {
            out.putNextEntry(new JarEntry(name));
            out.write("probed.key=jar-value".getBytes(StandardCharsets.UTF_8));
            out.closeEntry();
        }
        ClassLoader original = Thread.currentThread().getContextClassLoader();
        URLClassLoader loader = new URLClassLoader(new URL[] { jar.toUri().toURL() }, original);
        Thread.currentThread().setContextClassLoader(loader);
        try {
            assertTrue(FileIOAide.resourceExists(name), "jar 内资源必须可探测为存在");
            // 非文件形态 loadFile 仍为空（"无法作为文件定位"合法语义，不变）
            assertNull(FileIOAide.loadFile(name));
            Config config = ConfigLib.getConfig(name);
            assertFalse(config.keySet().isEmpty(), "jar 形态资源不得交付空表（原假成功面）");
            assertEquals("jar-value", config.getString("probed.key"), "必须交付包内真实内容");
        } finally {
            Thread.currentThread().setContextClassLoader(original);
            loader.close();
            Files.deleteIfExists(jar);
        }
    }

    /**
     * 资源确实不存在：getConfig 保持"文件缺失"合法分支的空配置语义（规格"假成功"收口仅指
     * 归档内确有资源却交付空表，不含本分支——此处钉桩防收口扩大化）。
     */
    @Test
    void absentResourceKeepsEmptyConfigBranch() {
        String name = "no-such-" + System.nanoTime() + ".properties";
        assertFalse(FileIOAide.resourceExists(name));
        Config config = ConfigLib.getConfig(name);
        assertNotNull(config);
        assertEquals(0, config.keySet().size(), "文件缺失仍交付空配置（现契约不变）");
    }

    /**
     * 转义（含空格）资源路径的监听登记（REAL_GAP 修复）：登记与定位必须同口径解码——
     * 原登记用字面 %20 路径，观察器永不命中。
     */
    @Test
    void escapedPathListenerFiresWithinPollCycle() throws Exception {
        FileIOAide.useMonitorIntervalForTest(100);
        String name = "io spaced listener.properties";
        AtomicInteger events = new AtomicInteger();
        FileAlterationListenerAdaptor listener = new FileAlterationListenerAdaptor() {
            @Override
            public void onFileCreate(File file) {
                events.incrementAndGet();
            }

            @Override
            public void onFileChange(File file) {
                events.incrementAndGet();
            }
        };
        FileIOAide.addFileListener(name, listener);
        Thread.sleep(500); // 观察器首个轮询周期做初快照，须待其完成后再触碰
        File target = FileIOAide.loadFile(name);
        assertNotNull(target, "测试前置：空格资源可定位");
        Files.write(target.toPath(), "listened.key=v2-touched-escape".getBytes(StandardCharsets.UTF_8));
        long deadline = System.currentTimeMillis() + 5000;
        while (events.get() == 0 && System.currentTimeMillis() < deadline) {
            Thread.sleep(50);
        }
        assertTrue(events.get() >= 1, "转义路径监听登记必须命中真实文件并回调（原字面 %20 永不命中）");
    }

    // ---- 复验收口轮：目录级监视单元 / 生命周期停止入口 / 系统属性热更重读 / jar 形态带监听告警 / 并发首载全钉 ----

    @TempDir
    Path tempDir;

    private URLClassLoader tempDirLoader;

    /** 临时目录挂为 classpath（先挂 classpath 再重建监视设施：轮询线程按创建时刻继承 TCCL） */
    private ClassLoader mountTempDir() throws IOException {
        final ClassLoader original = Thread.currentThread().getContextClassLoader();
        tempDirLoader = new URLClassLoader(new URL[] { tempDir.toUri().toURL() }, original);
        Thread.currentThread().setContextClassLoader(tempDirLoader);
        return original;
    }

    private void restore(ClassLoader original) throws IOException {
        Thread.currentThread().setContextClassLoader(original);
        if (tempDirLoader != null) {
            tempDirLoader.close();
            tempDirLoader = null;
        }
    }

    private void writeTemp(String name, String content) throws IOException {
        Files.write(tempDir.resolve(name), content.getBytes(StandardCharsets.UTF_8));
    }

    /** 有界等待条件成立（sleep 只是轮询步长，不是判定依据） */
    private static void awaitUntil(BooleanSupplier condition, long timeoutMillis) throws InterruptedException {
        final long deadline = System.currentTimeMillis() + timeoutMillis;
        while (System.currentTimeMillis() < deadline) {
            if (condition.getAsBoolean()) {
                return;
            }
            Thread.sleep(50);
        }
    }

    private static int countFileMonitorThreads() {
        int count = 0;
        for (final Thread thread : Thread.getAllStackTraces().keySet()) {
            if (thread.getName().startsWith("FileMonitorTread")) {
                count++;
            }
        }
        return count;
    }

    /** 文件级事件（创建+变更）计数：「恰一次回调/重读计数」断言依据 */
    private static final class CountingListener extends FileAlterationListenerAdaptor {

        private final AtomicInteger events = new AtomicInteger();

        @Override
        public void onFileCreate(File file) {
            this.events.incrementAndGet();
        }

        @Override
        public void onFileChange(File file) {
            this.events.incrementAndGet();
        }

        int count() {
            return this.events.get();
        }

    }

    /** 规格「同目录多文件共享一个目录级监视单元」：同目录两登记只建一个单元，仅改一个文件只触发其自身回调恰一次 */
    @Test
    void sameDirectoryFilesShareOneWatchUnit() throws Exception {
        final ClassLoader original = mountTempDir();
        try {
            FileIOAide.useMonitorIntervalForTest(100);
            final String nameA = "unit-a-" + System.nanoTime() + ".properties";
            final String nameB = "unit-b-" + System.nanoTime() + ".properties";
            writeTemp(nameA, "alpha=v1");
            writeTemp(nameB, "beta=v1");
            final CountingListener probeA = new CountingListener();
            final CountingListener probeB = new CountingListener();
            FileIOAide.addFileListener(nameA, probeA);
            FileIOAide.addFileListener(nameB, probeB);
            assertAll(
                    () -> assertEquals(1, FileIOAide.monitorObserverCountForTest(),
                            "同目录两文件 MUST 共享一个目录级监视单元（单目录一轮询周期只检视一次）"),
                    () -> assertEquals(2, FileIOAide.monitorListenerCountForTest(), "两登记均生效"));
            Thread.sleep(500); // 观察器初快照完成（前置条件，非判定 sleep）
            final int a0 = probeA.count();
            final int b0 = probeB.count();
            writeTemp(nameA, "alpha=v2-longer-content");
            awaitUntil(() -> probeA.count() > a0, 5000);
            Thread.sleep(400); // 再跨两个轮询周期的静默窗，确认无放大
            assertEquals(a0 + 1, probeA.count(), "A 文件变更只触发自身回调且恰一次，不随同目录文件数量放大");
            assertEquals(b0, probeB.count(), "仅触碰 A 文件，B 文件回调零触发");
        } finally {
            restore(original);
        }
    }

    /** 规格边界：裸文件名按当前目录登记不越界，并入同一监视单元；移除未登记路径不追加单元 */
    @Test
    void bareFileNameJoinsCurrentDirectoryUnit() {
        final FileMonitor monitor = new FileMonitor(100);
        try {
            final FileAlterationListenerAdaptor bare = new FileAlterationListenerAdaptor() {
            };
            final FileAlterationListenerAdaptor dotSlash = new FileAlterationListenerAdaptor() {
            };
            assertDoesNotThrow(() -> monitor.addFileListener("reverify-bare-a.txt", bare));
            assertDoesNotThrow(() -> monitor.addFileListener("./reverify-bare-b.txt", dotSlash));
            assertAll(
                    () -> assertEquals(1, monitor.observerCount(), "裸文件名与 ./ 前缀名同并当前目录一个监视单元"),
                    () -> assertEquals(2, monitor.listenerCount(), "两登记均生效"));
            monitor.removeFileListener("./reverify-bare-b.txt", dotSlash);
            monitor.removeFileListener("reverify-never-registered-" + System.nanoTime() + ".txt",
                    new FileAlterationListenerAdaptor() {
                    });
            assertAll(
                    () -> assertEquals(1, monitor.observerCount(), "移除未登记路径 MUST NOT 反而新建监视单元（移除不是追加）"),
                    () -> assertEquals(1, monitor.listenerCount(), "移除使登记数递减而非追加"));
        } finally {
            monitor.stop();
        }
    }

    /** 规格「登记与移除成对不累积泄漏」：增删全轮次归零、同文件同监听器幂等不双挂 */
    @Test
    void registrationsAndRemovalsPairWithoutAccumulation() {
        final FileMonitor monitor = new FileMonitor(100);
        try {
            final String fileA = "pair-a-" + System.nanoTime() + ".txt";
            final String fileB = "pair-b-" + System.nanoTime() + ".txt";
            final FileAlterationListenerAdaptor l1 = new FileAlterationListenerAdaptor() {
            };
            final FileAlterationListenerAdaptor l2 = new FileAlterationListenerAdaptor() {
            };
            final FileAlterationListenerAdaptor l3 = new FileAlterationListenerAdaptor() {
            };
            monitor.addFileListener(fileA, l1);
            monitor.addFileListener(fileA, l2);
            monitor.addFileListener(fileB, l3);
            assertEquals(3, monitor.listenerCount(), "三登记生效");
            monitor.addFileListener(fileA, l1);
            assertEquals(3, monitor.listenerCount(), "同文件同监听器重复登记幂等（不双挂、重读不双跑）");
            monitor.removeFileListener(fileA, l1);
            monitor.removeFileListener(fileA, l2);
            monitor.removeFileListener(fileB, l3);
            assertEquals(0, monitor.listenerCount(), "登记与移除成对：全部移除后零残留");
            monitor.removeFileListener(fileA, l1);
            assertEquals(0, monitor.listenerCount(), "重复移除为无害空操作（不追加不复活）");
        } finally {
            monitor.stop();
        }
    }

    /** 规格「多次带监听打开不新增轮询线程」：反复带监听打开资源，轮询线程数不随打开次数增长 */
    @Test
    void repeatedOpenWithListenerDoesNotAddPollerThreads() throws Exception {
        try (InputStream warmup = FileIOAide.openInputStream("io-system-guard.properties", new CountingListener())) {
            assertNotNull(warmup, "前置：默认 classpath 资源带监听打开成功");
        }
        final int baseline = countFileMonitorThreads();
        assertTrue(baseline >= 1, "前置：监视设施存在轮询线程");
        for (final String resource : new String[] { "io-import-main.properties", "io-cyclic-a.properties",
                "io-cyclic-b.properties" }) {
            try (InputStream in = FileIOAide.openInputStream(resource, new CountingListener())) {
                assertNotNull(in, resource + " 带监听打开成功");
            }
        }
        assertEquals(baseline, countFileMonitorThreads(),
                "反复带监听打开多份资源 MUST NOT 按打开次数新建轮询线程");
    }

    /** 规格「停止入口幂等且不逃逸异常」+「停止后回调静默」：停两次无异常，停止后触碰文件零回调、登记零残留 */
    @Test
    void globalMonitorStopIsIdempotentAndSilencesCallbacks() throws Exception {
        final ClassLoader original = mountTempDir();
        try {
            FileIOAide.useMonitorIntervalForTest(100);
            final String name = "lifecycle-" + System.nanoTime() + ".properties";
            writeTemp(name, "k=v1");
            final CountingListener probe = new CountingListener();
            FileIOAide.addFileListener(name, probe);
            Thread.sleep(500); // 初快照完成
            writeTemp(name, "k=v2-before-stop");
            awaitUntil(() -> probe.count() >= 1, 5000);
            assertTrue(probe.count() >= 1, "前置：设施在运行时变更必须回调");
            assertDoesNotThrow(FileIOAide::stopMonitor, "第一次停止 MUST NOT 抛异常");
            assertDoesNotThrow(FileIOAide::stopMonitor, "第二次停止 MUST 幂等且无异常逃逸");
            final int before = probe.count();
            writeTemp(name, "k=v3-after-stop");
            Thread.sleep(500); // 静默窗跨多个轮询周期
            assertEquals(before, probe.count(), "停止后既有监听回调 MUST NOT 再被触发");
            assertEquals(0, FileIOAide.monitorListenerCountForTest(), "停止后设施状态清零（登记不累积）");
        } finally {
            restore(original);
        }
    }

    /** 热更「新增+变更」：变更键读新值、新增键可读值正确；文件已删键保留最后装载值（不静默删除） */
    @Test
    void hotReloadAppliesAddsAndChangesButKeepsDeletedKeys() throws Exception {
        final String token = Long.toHexString(System.nanoTime());
        final String keyChanged = "reverify.io." + token + ".changed";
        final String keyAdded = "reverify.io." + token + ".added";
        final String keyDeleted = "reverify.io." + token + ".deleted";
        final ClassLoader original = mountTempDir();
        try {
            FileIOAide.useMonitorIntervalForTest(100); // 先挂 classpath 再建设施（轮询线程继承 TCCL）
            final String name = "sysprops-" + token + ".properties";
            writeTemp(name, keyChanged + "=first\n" + keyDeleted + "=kept\n");
            new SystemPropertiesLoader(name);
            assertEquals("first", System.getProperty(keyChanged));
            assertEquals("kept", System.getProperty(keyDeleted));
            Thread.sleep(500); // 初快照完成
            writeTemp(name, keyChanged + "=second\n" + keyAdded + "=fresh\n");
            awaitUntil(() -> "second".equals(System.getProperty(keyChanged))
                    && "fresh".equals(System.getProperty(keyAdded)), 8000);
            assertEquals("second", System.getProperty(keyChanged), "变更键重读后必须读到新值");
            assertEquals("fresh", System.getProperty(keyAdded), "新增键重读后可读且值正确");
            assertEquals("kept", System.getProperty(keyDeleted),
                    "文件已删除的键 MUST 保留最后装载值（热更为新增+变更，不静默删除）");
        } finally {
            restore(original);
            for (final String key : new String[] { keyChanged, keyAdded, keyDeleted }) {
                System.clearProperty(key);
            }
        }
    }

    /** 热更触发同样守卫：越界键被拒绝且可观测、原值不变；命名空间内键正常更新 */
    @Test
    void hotReloadTriggersSameNamespaceGuard() throws Exception {
        final String token = Long.toHexString(System.nanoTime());
        final String keySafe = "reverify.io." + token + ".safe";
        final String keyMark = "reverify.io." + token + ".mark";
        final ClassLoader original = mountTempDir();
        try {
            FileIOAide.useMonitorIntervalForTest(100);
            final String name = "sysguard-" + token + ".properties";
            writeTemp(name, keySafe + "=1\n");
            new SystemPropertiesLoader(name);
            final String homeBefore = System.getProperty("user.home");
            Thread.sleep(500); // 初快照完成
            writeTemp(name, keySafe + "=2\n" + keyMark + "=ok\nuser.home=/tmp/reverify-evil-home\n");
            awaitUntil(() -> "2".equals(System.getProperty(keySafe)) && "ok".equals(System.getProperty(keyMark)),
                    8000);
            assertSame(homeBefore, System.getProperty("user.home"), "热更重读 MUST 同守卫：越界键原值不得被改写");
            assertTrue(SystemPropertiesLoader.lastRejectedKeys().contains("user.home"),
                    "热更路径的拒绝键必须可观测（显式告警，非静默丢弃）");
        } finally {
            restore(original);
            System.clearProperty(keySafe);
            System.clearProperty(keyMark);
        }
    }

    /** JVM 原生属性全程不受牵连：连续多轮热更期间前后快照完全一致 */
    @Test
    void jvmNativePropertiesUntouchedAcrossReloadRounds() throws Exception {
        final String token = Long.toHexString(System.nanoTime());
        final String keyMark = "reverify.io." + token + ".mark";
        final String keyEvilAllowed = "reverify.io." + token + ".evil-allowed";
        final List<String> nativeKeys = Arrays.asList("java.class.path", "user.home", "user.dir", "os.name",
                "file.separator", "path.separator", "line.separator");
        final Map<String, String> snapshotBefore = new HashMap<>();
        for (final String key : nativeKeys) {
            snapshotBefore.put(key, System.getProperty(key));
        }
        final ClassLoader original = mountTempDir();
        try {
            FileIOAide.useMonitorIntervalForTest(100);
            final String name = "sysnative-" + token + ".properties";
            writeTemp(name, keyMark + "=1\n");
            new SystemPropertiesLoader(name);
            Thread.sleep(500); // 初快照完成
            writeTemp(name, keyMark + "=2\nuser.home=/tmp/reverify-evil\njava.class.path=/tmp/evil.jar\n"
                    + keyEvilAllowed + "=1\n");
            awaitUntil(() -> "2".equals(System.getProperty(keyMark)), 8000);
            assertNativeUnchanged(nativeKeys, snapshotBefore);
            writeTemp(name, keyMark + "=3\nos.name=evil-os\nuser.dir=/tmp/evil-dir\n");
            awaitUntil(() -> "3".equals(System.getProperty(keyMark)), 8000);
            assertNativeUnchanged(nativeKeys, snapshotBefore);
        } finally {
            restore(original);
            System.clearProperty(keyMark);
            System.clearProperty(keyEvilAllowed);
        }
    }

    private static void assertNativeUnchanged(List<String> keys, Map<String, String> snapshot) {
        for (final String key : keys) {
            assertEquals(snapshot.get(key), System.getProperty(key),
                    "原生键 " + key + " 不得被多轮热更删除/改写/置空");
        }
    }

    /** 规格「不可监听形态显式告警而非静默失效」：jar 内资源带监听打开——内容逐字节一致、告警可断言、零登记 */
    @Test
    void jarResourceOpenedWithListenerWarnsAndRegistersNothing() throws Exception {
        final byte[] expected = ("probed.key=jar-value-" + System.nanoTime()).getBytes(StandardCharsets.UTF_8);
        final String name = "jarlisten-" + System.nanoTime() + ".properties";
        final Path jar = Files.createTempFile("cfg-listen", ".jar");
        try (JarOutputStream out = new JarOutputStream(Files.newOutputStream(jar))) {
            out.putNextEntry(new JarEntry(name));
            out.write(expected);
            out.closeEntry();
        }
        final ClassLoader original = Thread.currentThread().getContextClassLoader();
        final URLClassLoader loader = new URLClassLoader(new URL[] { jar.toUri().toURL() }, original);
        Thread.currentThread().setContextClassLoader(loader);
        try {
            FileIOAide.useMonitorIntervalForTest(100); // 全新设施：登记计数断言起点为零
            FileIOAide.clearUnmonitorableRegistrationsForTest();
            final byte[] plain;
            try (InputStream in = FileIOAide.openInputStream(name)) {
                assertNotNull(in, "不带监听打开归档内容可读");
                plain = in.readAllBytes();
            }
            final CountingListener probe = new CountingListener();
            final byte[] listened;
            try (InputStream in = FileIOAide.openInputStream(name, probe)) {
                assertNotNull(in, "对归档内资源带监听方式打开 MUST 仍交付内容");
                listened = in.readAllBytes();
            }
            assertAll(
                    () -> assertArrayEquals(expected, plain, "归档内内容必须逐字节忠实读出"),
                    () -> assertArrayEquals(plain, listened, "带监听与不带监听读到的内容 MUST 逐字节一致"),
                    () -> assertTrue(FileIOAide.unmonitorableRegistrationsForTest().contains(name),
                            "不可监听形态 MUST 显式告警热更不可用（可断言观测面与日志告警配套，非静默丢弃）"),
                    () -> assertEquals(0, FileIOAide.monitorListenerCountForTest(),
                            "MUST NOT 存在任何挂在伪路径上的监听登记"),
                    () -> assertEquals(0, probe.count(), "零登记即零回调"));
        } finally {
            Thread.currentThread().setContextClassLoader(original);
            loader.close();
            Files.deleteIfExists(jar);
        }
    }

    /** 规格「并发首载单实例单监听」全量钉：八线程栅栏并发首载——单实例、监听登记恰一份、随后一次变更重读恰一次 */
    @Test
    void concurrentFirstLoadPinsOneListenerAndOneReload() throws Exception {
        final ClassLoader original = mountTempDir();
        try {
            FileIOAide.useMonitorIntervalForTest(100);
            final String name = "race-" + System.nanoTime() + ".properties";
            writeTemp(name, "k=v1");
            assertEquals(0, FileIOAide.listenerRegistrationCountForTest(name), "前置：该路径尚未有监听登记");
            final int threads = 8;
            final ClassLoader mounted = Thread.currentThread().getContextClassLoader();
            final CyclicBarrier barrier = new CyclicBarrier(threads);
            final ExecutorService pool = Executors.newFixedThreadPool(threads);
            final Config config;
            try {
                final List<Future<Config>> futures = new ArrayList<>();
                for (int t = 0; t < threads; t++) {
                    futures.add(pool.submit(() -> {
                        barrier.await(5, TimeUnit.SECONDS);
                        Thread.currentThread().setContextClassLoader(mounted);
                        return ConfigLib.getConfig(name);
                    }));
                }
                final Set<Config> distinct = new HashSet<>();
                for (final Future<Config> future : futures) {
                    distinct.add(future.get(10, TimeUnit.SECONDS));
                }
                assertEquals(1, distinct.size(), "并发首载 MUST 收敛为单实例");
                config = distinct.iterator().next();
            } finally {
                pool.shutdownNow();
            }
            assertAll(
                    () -> assertEquals(1, FileIOAide.listenerRegistrationCountForTest(name),
                            "并发首载 MUST 只完成一次监听登记（原双登记面）"),
                    () -> assertEquals(1, FileIOAide.monitorListenerCountForTest(), "全新设施总登记恰此一份"));
            Thread.sleep(500); // 初快照完成
            final CountingListener probe = new CountingListener();
            FileIOAide.addFileListener(name, probe); // 同路径探针与首载监听共享投递路径
            writeTemp(name, "k=v2-longer");
            awaitUntil(() -> probe.count() >= 1, 8000);
            Thread.sleep(400); // 静默窗
            assertAll(
                    () -> assertEquals(1, probe.count(), "一次真实文件变更 MUST 恰触发一次重读投递（重读计数为 1 而非 2）"),
                    () -> assertEquals("v2-longer", config.getString("k"), "重读后内容正常更新"));
        } finally {
            restore(original);
        }
    }

    // ---- 子配置查找作用域（首轮遗留 D12）----

    /** 子配置的 find 只作用于本前缀空间（原透传父级返回全库匹配） */
    @Test
    void childFindScopedToSubspace() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("l1.a.key", "1");
        data.put("l1.b.key", "2");
        data.put("l2.a.key", "3");
        Config root = ConfigLib.newConfig(data);
        Config l1 = root.child("l1");
        Map<String, Object> hits = l1.find(".*a\\.key");
        assertEquals(Set.of("l1.a.key"), hits.keySet(),
                "子配置 find 泄漏了他子空间键（原实现返回全库匹配）");
    }

    /** 子配置 entrySet/keySet 返回绝对键（现契约钉桩：与取值路径一致） */
    @Test
    void childViewsKeepAbsoluteKeys() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("l1.a.key", "1");
        data.put("other.key", "2");
        Config l1 = ConfigLib.newConfig(data).child("l1");
        assertEquals(Set.of("l1.a.key"), l1.keySet());
    }

}
