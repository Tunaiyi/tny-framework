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
package com.tny.game.it.assembly;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

/**
 * demo 官方应用的子进程载体（design D2 实施修订，用户批准）：boot 的
 * {@code ApplicationLauncherContext}/单元表为 JVM 级静态——同 JVM 双官方应用结构不可行
 * （实证：第二上下文连带实例化第一应用实体 bean 且 applicationLauncherLifecycle 二次启动
 * 失败），故每个应用一个进程，即 demo 的真实部署形态。
 * <p>
 * 启动序与 demo main 一致（主类直跑），profile 与测试装配文件（classpath 内
 * {@code it-*.yml}，additional-location 高优先级）+ 运行时 {@code --it.*} 参数全部走
 * 命令行——占位符缺参即启动期解析失败，天然满足"装配缺项可定位暴露"。
 * stdout/stderr 合流重定向到日志文件，失败可携尾段断言。
 */
public final class DemoAppProcess implements AutoCloseable {

    private static final long READY_TIMEOUT_MILLIS = 60_000L;

    private final Process process;

    private final Path logFile;

    private DemoAppProcess(Process process, Path logFile) {
        this.process = process;
        this.logFile = logFile;
    }

    /**
     * 以 demo 主类起子进程并等待其网络端口可连通。
     *
     * @param mainClass  demo 应用主类（自带 main）
     * @param profile    spring profile（demo 既有 yml 的激活名）
     * @param configFile 测试装配覆盖文件绝对路径（{@code file:} 注入使子进程 classpath 与
     *                   测试模块产物解耦——隔离 classpath 下 classpath 资源不再可见）
     * @param runtime    运行时参数表（端口/容器地址），以 {@code --key=value} 追加
     */
    public static DemoAppProcess launch(Class<?> mainClass, String profile, Path configFile,
            Map<String, String> runtime) throws IOException {
        List<String> command = new ArrayList<>();
        command.add(Path.of(System.getProperty("java.home"), "bin", "java").toString());
        command.add("-cp");
        // 受控 classpath（it.demo.isolatedClasspath＝IT 产物+demo jar）：既避免 java.class.path
        // 把并行会话在途未编译稳的新类泄入子进程（@UnitInterface 缺位实证破坏 @Unit 注册），
        // 也避免纯 maven-jar 隔离的旧快照漂移——demo 主类经 IT classes 先行解析
        String isolated = System.getProperty("it.demo.isolatedClasspath");
        command.add(isolated != null && !isolated.isBlank() ? isolated : System.getProperty("java.class.path"));
        // 显式钉死测试日志配置（file: 绝对路径，同 configFile 理由）：log4j2 默认发现序 json 先于
        // xml，否则 demo 的坏 log4j2.json 胜出、bundle 查找抛噪音且业务日志（认证拒绝面）全灭
        Path logCfg = Path.of(DemoAppProcess.class.getResource("/log4j2.xml").getPath());
        command.add("-Dlog4j2.configurationFile=file:" + logCfg.toAbsolutePath());
        command.add(mainClass.getName());
        command.add("--spring.profiles.active=" + profile);
        command.add("--spring.config.additional-location=file:" + configFile.toAbsolutePath());
        runtime.forEach((key, value) -> command.add("--" + key + "=" + value));

        Path logDir = Path.of("build", "it-logs");
        Files.createDirectories(logDir);
        Path log = logDir.resolve("it-" + mainClass.getSimpleName() + "-" + System.nanoTime() + ".log");
        System.out.println("CMD " + mainClass.getSimpleName() + " args=" + command.subList(command.size() - runtime.size() - 3, command.size()));
        Process process = new ProcessBuilder(command)
                .redirectErrorStream(true)
                .redirectOutput(log.toFile())
                .start();
        // stdin 保持打开且不写入：demo 主线程 waitForConsole 的 readLine 阻塞即保活
        return new DemoAppProcess(process, log);
    }

    /** 等待给定端口集合全部可连通；超时即携日志尾段失败（装配缺陷定位面）。 */
    public void awaitListening(Collection<Integer> ports) throws InterruptedException {
        long deadline = System.currentTimeMillis() + READY_TIMEOUT_MILLIS;
        List<Integer> pending = new ArrayList<>(ports);
        while (!pending.isEmpty() && System.currentTimeMillis() < deadline) {
            pending.removeIf(port -> acceptProbe(port));
            if (!pending.isEmpty()) {
                if (!process.isAlive()) {
                    break;
                }
                // 绑定诊断：guide 日志出现全部期望端口即就绪（防本地 connect 探测被
                // 其他在跑进程会话误满足导致的漂移错觉）
                String log = logTail(Integer.MAX_VALUE);
                if (log.contains("端口已监听") && ports.stream().allMatch(p -> log.contains(":" + p + "端口已监听"))) {
                    pending.clear();
                    break;
                }
                Thread.sleep(200);
            }
        }
        if (!pending.isEmpty()) {
            if (!process.isAlive()) {
                throw new IllegalStateException("子进程 " + processTitle() + " 启动失败（退出码 " + process.exitValue()
                        + "），端口未就绪: " + pending + System.lineSeparator() + logTail(4000));
            }
            throw new IllegalStateException("子进程 " + processTitle() + " 端口 60s 未就绪: " + pending
                    + System.lineSeparator() + logTail(4000));
        }
    }

    /**
     * 等待本进程日志出现"出站中继链路建立成功"（剧本竞态防线，见
     * {@code RelayLoginScenarioIT}）。命中即返回；超时/进程退出携日志尾段失败。
     */
    public void awaitRelayLink() throws InterruptedException {
        long deadline = System.currentTimeMillis() + READY_TIMEOUT_MILLIS;
        while (System.currentTimeMillis() < deadline) {
            String log = logTail(Integer.MAX_VALUE);
            if (log.contains("NettyRelayServeInstance") && log.contains("success on the")) {
                return;
            }
            if (!process.isAlive()) {
                throw new IllegalStateException("子进程 " + processTitle() + " 已退出，中继链路未建立"
                        + System.lineSeparator() + logTail(4000));
            }
            Thread.sleep(200);
        }
        throw new IllegalStateException("子进程 " + processTitle() + " 中继链路 60s 未建立"
                + System.lineSeparator() + logTail(4000));
    }

    public boolean isAlive() {
        return process.isAlive();
    }

    public int exitValue() {
        return process.exitValue();
    }

    /** 有限等待进程退出（装配失败路径断言"即时失败而非挂起"）。 */
    public boolean waitForExit(long millis) throws InterruptedException {
        return process.waitFor(millis, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    public String logTail(int maxChars) {
        try {
            String all = Files.readString(logFile, StandardCharsets.UTF_8);
            return all.length() <= maxChars ? all : all.substring(all.length() - maxChars);
        } catch (IOException e) {
            return "<日志不可读: " + logFile + ">";
        }
    }

    @Override
    public void close() {
        process.destroy();
        try {
            if (!process.waitFor(10, java.util.concurrent.TimeUnit.SECONDS)) {
                process.destroyForcibly();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            process.destroyForcibly();
        }
        // 日志保留在 build/it-logs/ 供失败诊断（目录随 gradle clean 回收）
    }

    private String processTitle() {
        return "java " + process.pid();
    }

    private static boolean acceptProbe(int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(DemoApps.HOST, port), 300);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
