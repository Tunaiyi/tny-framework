package com.tny.game.common.utils;

import com.tny.game.common.concurrent.ThrowableCallable;
import com.tny.game.common.concurrent.ExeUncheckedException;
import com.tny.game.common.concurrent.ThrowableRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            logger.error("run {} exception", runnable.getClass(), e);
        }
    }

    static void runQuietly(Runnable runnable) {
        runQuietly(runnable, LOGGER);
    }

    static void runUnchecked(ThrowableRunnable runnable, Logger logger) {
        try {
            runnable.run();
        } catch (Throwable e) {
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

}
