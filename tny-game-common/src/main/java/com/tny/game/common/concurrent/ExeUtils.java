package com.tny.game.common.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * Created by Kun Yang on 16/8/31.
 */
public interface ExeUtils {

    Logger LOGGER = LoggerFactory.getLogger(ExeUtils.class);

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

}
