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

package com.tny.game.common.concurrent.utils;

import com.tny.game.common.concurrent.*;
import org.slf4j.*;

import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * Created by Kun Yang on 16/8/31.
 */
public interface ExeAide {

    Logger LOGGER = LoggerFactory.getLogger(ExeAide.class);

    static void runQuietly(Runnable runnable, Logger logger) {
        try {
            runnable.run();
        } catch (Throwable e) {
            keepInterrupt(e);
            logger.error("run {} exception", runnable.getClass(), e);
        }
    }

    /**
     * 静默/上抛工具统一收口：异常因果链含中断时必须恢复线程中断标志
     * （取消语义依赖标志传播，吞中断=关停钩子被中断后静默继续）。
     */
    static void keepInterrupt(Throwable e) {
        for (Throwable t = e; t != null; t = t.getCause() == t ? null : t.getCause()) {
            if (t instanceof InterruptedException) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    static void runQuietly(Runnable runnable) {
        runQuietly(runnable, LOGGER);
    }

    static void runUnchecked(ThrowableRunnable runnable, Logger logger) {
        try {
            runnable.run();
        } catch (Throwable e) {
            keepInterrupt(e);
            logger.error("run {} exception", runnable.getClass(), e);
            throw new ExeUncheckedException(e);
        }
    }

    static void runUnchecked(ThrowableRunnable runnable) {
        runUnchecked(runnable, LOGGER);
    }

    static <R> Optional<R> callQuietly(Callable<R> callable, R defReturn, Logger logger) {
        try {
            return Optional.ofNullable(callable.call());
        } catch (Throwable e) {
            keepInterrupt(e);
            logger.error("run {} exception", callable.getClass(), e);
            return Optional.ofNullable(defReturn);
        }
    }

    static <R> Optional<R> callQuietly(Callable<R> callable, R defReturn) {
        return callQuietly(callable, defReturn, LOGGER);
    }

    static <R> Optional<R> callQuietly(Callable<R> callable) {
        return callQuietly(callable, null, LOGGER);
    }

    static <R> Optional<R> callUnchecked(ThrowableCallable<R> callable, Logger logger) {
        try {
            return Optional.ofNullable(callable.call());
        } catch (Exception e) {
            logger.error("run {} exception", callable.getClass(), e);
            throw new ExeUncheckedException(e);
        }
    }

    static <R> Optional<R> callUnchecked(ThrowableCallable<R> callable) {
        return callUnchecked(callable, LOGGER);
    }

    static <R> R callNullableWithUnchecked(ThrowableCallable<R> callable, Logger logger) {
        try {
            return callable.call();
        } catch (Exception e) {
            logger.error("run {} exception", callable.getClass(), e);
            throw new ExeUncheckedException(e);
        }
    }

    static <R> R callNullableWithUnchecked(ThrowableCallable<R> callable) {
        return callNullableWithUnchecked(callable, LOGGER);
    }

}
