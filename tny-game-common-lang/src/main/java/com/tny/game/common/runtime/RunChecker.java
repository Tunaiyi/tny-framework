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

import com.tny.game.common.utils.*;
import org.slf4j.*;

import java.util.*;

public class RunChecker {

    private static final Logger LOGGER = LoggerFactory.getLogger(RunChecker.class);

    private static final ThreadLocal<Map<Object, ProcessTracer>> TRACER_THREAD_LOCAL = ThreadLocal.withInitial(HashMap::new);

    private static final ProcessWatcher DEFAULT_WATCHER = ProcessWatcher.getDefault();

    /** 未开始即结束的哨兵：链式取耗时不得 NPE（读数为 0，与真实测量区分由告警承担） */
    private static final ProcessTracer NOT_STARTED = ProcessTracer.notStarted();

    private static ProcessTracer doTrace(Object target, TrackPrintOption option, String message, Object... params) {
        Map<Object, ProcessTracer> counterMap = TRACER_THREAD_LOCAL.get();
        ProcessTracer tracer = counterMap.get(target);
        // 残留判定并入完成态：上一周期已结束（end 未回收的旁路）不误报"没有结束"
        if (tracer != null && !tracer.isDone()) {
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
        Map<Object, ProcessTracer> counterMap = TRACER_THREAD_LOCAL.get();
        ProcessTracer tracer = counterMap.get(target);
        if (tracer == null) {
            // 旁路结束（如 try-with-resources 的 close 先于本调用）：按句柄完成态判定，
            // 已完成=合法结束静默幂等；仅真正从未跟踪时告警
            ProcessTracer any = counterMap.values().stream()
                    .filter(t -> t != null && target.equals(t.getWatcher())).findFirst().orElse(null);
            if (any != null && any.isDone()) {
                counterMap.remove(target, any);
                return any;
            }
            LOGGER.warn("{} 任务未开始!!", target);
            // 哨兵返回：调用方 end(target).costMicroTime() 链式取数不再 NPE（原返回 null）
            return NOT_STARTED;
        }
        // 结束动作真实回收登记（原 doDone 用错误键且无人调用，逐目标泄漏）
        counterMap.remove(target, tracer);
        return tracer.done(message, args);
    }

}
