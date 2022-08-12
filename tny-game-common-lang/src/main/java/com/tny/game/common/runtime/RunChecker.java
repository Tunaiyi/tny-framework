/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.common.runtime;

import com.tny.game.common.utils.*;
import org.slf4j.*;

import java.util.*;

public class RunChecker {

    private static final Logger LOGGER = LoggerFactory.getLogger(RunChecker.class);

    private static final ThreadLocal<Map<Object, ProcessTracer>> TRACER_THREAD_LOCAL = ThreadLocal.withInitial(HashMap::new);

    private static void doDone(ProcessTracer tracer) {
        TRACER_THREAD_LOCAL.get().remove(tracer.getId(), tracer);
    }

    private static final ProcessWatcher DEFAULT_WATCHER = ProcessWatcher.getDefault();

    private static ProcessTracer doTrace(Object target, TrackPrintOption option, String message, Object... params) {
        Map<Object, ProcessTracer> counterMap = TRACER_THREAD_LOCAL.get();
        ProcessTracer tracer = counterMap.get(target);
        if (tracer != null) {
            LOGGER.warn("{} 任务没有结束!!", target);
        }
        if (StringAide.isNoneBlank(message)) {
            tracer = DEFAULT_WATCHER.trace(target.toString(), message, params);
        } else {
            tracer = DEFAULT_WATCHER.trace(target.toString(), option);
        }
        counterMap.put(target, tracer);
        return tracer.start(message, params);
    }

    public static ProcessTracer trace(Object target) {
        return doTrace(target, TrackPrintOption.CLOSE, null);
    }

    public static ProcessTracer trace(Object target, TrackPrintOption option) {
        return doTrace(target, option, null);
    }

    public static ProcessTracer traceWithPrint(Object target) {
        return doTrace(target, TrackPrintOption.ALL, null);
    }

    public static ProcessTracer traceWithPrint(Object target, String message, Object... args) {
        return doTrace(target, TrackPrintOption.ALL, message, args);
    }

    public static boolean isTracing(Object target) {
        ProcessTracer tracer = TRACER_THREAD_LOCAL.get().get(target);
        return tracer != null && !tracer.isDone();
    }

    public static ProcessTracer end(Object target) {
        return doEnd(target, null);
    }

    public static ProcessTracer end(Object target, String message, Object... args) {
        return doEnd(target, message, args);
    }

    private static ProcessTracer doEnd(Object target, String message, Object... args) {
        ProcessTracer tracer = TRACER_THREAD_LOCAL.get().get(target);
        if (tracer == null) {
            LOGGER.warn("{} 任务未开始!!", target);
            return null;
        }
        return tracer.done(message, args);
    }

}
