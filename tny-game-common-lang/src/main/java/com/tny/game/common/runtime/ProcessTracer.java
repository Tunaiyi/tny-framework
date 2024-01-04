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

    private final Object watcher;

    private final Object id;

    private long startAt = -1;

    private long endAt = -1;

    private final Logger logger;

    private final TrackPrintOption printOption;

    private final TraceOnDone callback;

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
                                FORMATTER.format(Instant.ofEpochMilli(TimeUnit.MICROSECONDS.toMillis(this.startAt))))
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
                                FORMATTER.format(Instant.ofEpochMilli(TimeUnit.NANOSECONDS.toMillis(this.endAt))))
                        .append(message, params));
            }
        }
    }

    public long costMicroTime() {
        return this.endAt - this.startAt;
    }

    public long costMillisTime() {
        return TimeUnit.MICROSECONDS.toMillis(this.endAt - this.startAt);
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
