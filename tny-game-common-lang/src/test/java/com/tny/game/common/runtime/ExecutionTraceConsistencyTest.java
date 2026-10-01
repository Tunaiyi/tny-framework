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
package com.tny.game.common.runtime;

import org.junit.jupiter.api.*;

import java.io.*;
import java.time.*;
import java.time.format.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * execution-tracing-consistency 契约（fix-common-dormant-defects 组11）：
 * 完整周期重入不误报且条目随结束回收、起止日志同单位同基准、未开始结束以哨兵安全收口。
 */
class ExecutionTraceConsistencyTest {

    /** slf4j-simple 输出走 System.err：定向捕获告警/日志文本 */
    private static String captureErr(Runnable body) {
        PrintStream original = System.err;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            System.setErr(new PrintStream(buffer, true));
            body.run();
        } finally {
            System.setErr(original);
        }
        return buffer.toString();
    }

    @Test
    void completedCycleReentryDoesNotWarnAndReclaims() {
        Object target = "trace-cycle-target";
        // 第一周期
        RunChecker.trace(target);
        RunChecker.end(target);
        // 第二周期开始：不得出现"任务没有结束"误报（原实现残留判定未并完成态）
        String output = captureErr(() -> RunChecker.trace(target));
        assertFalse(output.contains("任务没有结束"),
                "完整周期后重入误报未结束：\n" + output);
        RunChecker.end(target);
        assertFalse(RunChecker.isTracing(target), "结束未回收登记（逐周期泄漏）");
        // 连续多周期不累积
        for (int i = 0; i < 50; i++) {
            RunChecker.trace(target);
            RunChecker.end(target);
        }
        assertFalse(RunChecker.isTracing(target));
    }

    @Test
    void genuinelyUnfinishedCycleStillWarns() {
        Object target = "trace-unfinished-target";
        String output = captureErr(() -> {
            RunChecker.trace(target);
            RunChecker.trace(target); // 上一周期确实未 end：必须告警
        });
        assertTrue(output.contains("任务没有结束"), "真实未闭合周期必须保留告警：\n" + output);
        RunChecker.end(target);
    }

    /** 未开始即结束：哨兵返回，链式耗时读数安全为 0（不 NPE、不产出天文数字） */
    @Test
    void endWithoutStartReturnsSafeSentinel() {
        ProcessTracer tracer = assertDoesNotThrow(() -> RunChecker.end("never-started-target"));
        assertNotNull(tracer, "未开始结束必须返回可安全使用的哨兵（原返回 null 致链式 NPE）");
        assertEquals(0, tracer.costMicroTime());
        assertEquals(0, tracer.costMillisTime());
    }

    /** 起止时间戳同单位同基准：end 打印的时刻与 start 打印的时刻差 ≈ 真实耗时（原差千倍） */
    @Test
    void startAndEndTimestampsShareUnitAndBase() throws Exception {
        java.util.concurrent.atomic.AtomicReference<ProcessTracer> held = new java.util.concurrent.atomic.AtomicReference<>();
        String output = captureErr(() -> {
            // trace(...) 即开始（start 由 watcher 内部触发并打印）
            ProcessTracer tracer = newTracer(TrackPrintOption.ALL);
            held.set(tracer);
            sleepQuietly(120);
            tracer.done();
        });
        ProcessTracer tracer = held.get();
        long startStamp = extractMillis(output, "开始 [>>] : ");
        long endStamp = extractMillis(output, "结束 [!!] : ");
        long costMillis = tracer.costMillisTime();
        assertTrue(startStamp > 1_600_000_000_000L, "开始时刻未落在墙钟区间: " + startStamp);
        assertTrue(endStamp > 1_600_000_000_000L,
                "结束时刻单位/基准与开始不一致（原微秒当纳秒致 1970 附近）: " + endStamp);
        long gap = endStamp - startStamp;
        assertTrue(Math.abs(gap - costMillis) < 250,
                "起止时间戳差 " + gap + "ms 与耗时 " + costMillis + "ms 不吻合");
    }

    private static ProcessTracer newTracer(TrackPrintOption option) {
        // 经公共入口取一个真 watcher 句柄
        ProcessWatcher watcher = ProcessWatcher.of("unit-base-probe-" + System.nanoTime());
        return watcher.trace("probe", option);
    }

    private static final DateTimeFormatter LOG_STAMP =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(ZoneId.systemDefault());

    private static long extractMillis(String output, String marker) {
        int at = output.indexOf(marker);
        assertTrue(at >= 0, "缺少 " + marker + " 输出：\n" + output);
        String stamped = output.substring(at + marker.length());
        // 时间戳恰为 "yyyy-MM-dd HH:mm:ss.SSS" 23 字符
        String text = stamped.substring(0, 23);
        try {
            return Instant.from(LOG_STAMP.parse(text)).toEpochMilli();
        } catch (Exception e) {
            throw new AssertionError("解析时间戳失败: '" + text + "'", e);
        }
    }

    private static void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
