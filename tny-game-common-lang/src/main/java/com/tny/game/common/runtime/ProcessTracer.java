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

package com.tny.game.common.runtime;

import org.slf4j.LoggerFactory;

import org.slf4j.Logger;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/10 5:41 下午
 */
public class ProcessTracer implements AutoCloseable {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
            .withZone(ZoneId.systemDefault());

    /** nanoTime 时基 → 墙钟毫秒的换算偏移（类加载期定格；起止日志同用，声明为墙钟估计） */
    private static final long WALL_CLOCK_OFFSET =
            System.currentTimeMillis() - TimeUnit.NANOSECONDS.toMillis(System.nanoTime());

    private final Object watcher;

    private final Object id;

    private long startAt = -1;

    private long endAt = -1;

    private final Logger logger;

    private final TrackPrintOption printOption;

    private final TraceOnDone callback;

    /** 观测目标（登记键）回读，供 RunChecker 结束路径做旁路判定与精确回收 */
    Object getWatcher() {
        return this.watcher;
    }

    ProcessTracer(Object watcher, Object id, Logger logger, TrackPrintOption printOption, TraceOnDone callback) {
        super();
        this.id = id;
        this.watcher = watcher;
        this.logger = logger;
        this.printOption = printOption;
        this.callback = callback;
    }

    ProcessTracer start() {
        return start(null);
    }

    ProcessTracer start(String message, Object... params) {
        if (this.startAt == -1) {
            this.startAt = TimeUnit.NANOSECONDS.toMicros(System.nanoTime());
            if (this.printOption.isOnStart()) {
                this.log(LogFragment
                        .message("执行监控 [ {} ] 跟踪执行 < {} > | 开始 [>>] : {}", this.watcher, this.getId(),
                                // 微秒时值换算墙钟毫秒（与结束侧同一单位与基准）
                                FORMATTER.format(Instant.ofEpochMilli(
                                        TimeUnit.MICROSECONDS.toMillis(this.startAt) + WALL_CLOCK_OFFSET)))
                        .append(message, params));
            }
        }
        return this;
    }

    public Object getId() {
        return this.id;
    }

    public long getStartAt() {
        return this.startAt;
    }

    public long getEndAt() {
        return this.endAt;
    }

    private void end(String message, Object... params) {
        if (this.endAt == -1) {
            this.endAt = TimeUnit.NANOSECONDS.toMicros(System.nanoTime());
            if (this.callback != null) {
                this.callback.onDone(this);
            }
            if (this.printOption.isOnEnd()) {
                this.log(LogFragment
                        .message("执行监控 [ {} ] 跟踪执行 < {} > | 结束 [!!] : {}", this.watcher, this.getId(),
                                // 原 NANOSECONDS.toMillis(endAt) 把微秒当纳秒：与开始侧差千倍
                                FORMATTER.format(Instant.ofEpochMilli(
                                        TimeUnit.MICROSECONDS.toMillis(this.endAt) + WALL_CLOCK_OFFSET)))
                        .append(message, params));
            }
        }
    }

    /** 未开始即结束/未结束等无效态的安全哨兵（读数 0，供 RunChecker 未开始路径返回） */
    static ProcessTracer notStarted() {
        ProcessTracer sentinel = new ProcessTracer("N/A", "N/A",
                LoggerFactory.getLogger(ProcessTracer.class), TrackPrintOption.CLOSE, null);
        sentinel.startAt = 0;
        sentinel.endAt = 0;
        return sentinel;
    }

    public long costMicroTime() {
        if (this.startAt < 0 || this.endAt < 0) {
            // 未开始/未结束的差值拼出天文数字=无效观测；显式失败（哨兵实例读 0）
            throw new IllegalStateException("跟踪未处于完整周期: startAt=" + this.startAt + ", endAt=" + this.endAt);
        }
        return this.endAt - this.startAt;
    }

    public long costMillisTime() {
        return TimeUnit.MICROSECONDS.toMillis(costMicroTime());
    }

    public ProcessTracer done() {
        return done(null);
    }

    public ProcessTracer done(String message, Object... params) {
        this.end(message, params);
        if (this.printOption.isOnSettle()) {
            this.log("执行监控 [ {} ] 跟踪执行 < {} > | 执行耗时 [##] : {} us", this.watcher, this.getId(), this.costMicroTime());
        }
        return this;
    }

    @Override
    public void close() {
        this.done();
    }

    public boolean isDone() {
        return this.endAt > 0;
    }

    private static boolean isEmpty(Object[] params) {
        return params == null || params.length == 0;
    }

    private void log(LogFragment fragment) {
        fragment.log(this.logger);
    }

    private void log(String message, Object... params) {
        this.logger.debug(message, params);
    }

}
