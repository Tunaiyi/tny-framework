package com.tny.game.common.concurrent;

import org.junit.jupiter.api.*;
import org.slf4j.*;

import java.util.concurrent.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/7/28 15:15
 **/
class CompleteFutureAideTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompleteFutureAideTest.class);

    private static final Executor EXECUTOR = Executors.newFixedThreadPool(2, CoreThreadFactory.daemonWithName(CompleteFutureAideTest.class));

    private static final Executor DELAY_EXECUTOR = Executors.newFixedThreadPool(2, CoreThreadFactory.daemonWithName("DelayTest"));

    @Test
    void noopComplete() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        var delayedExecutor = CompletableFuture.delayedExecutor(3, TimeUnit.SECONDS, DELAY_EXECUTOR);
        CompletableFuture
                .runAsync(() -> LOGGER.info("runAsync 1 {}" + Thread.currentThread().getName()))
                .whenCompleteAsync(CompleteFutureAide.noopComplete(), EXECUTOR)
                .thenAccept((v) -> LOGGER.info("runAsync 2 {}" + Thread.currentThread().getName()))
                .thenAccept((v) -> LOGGER.info("runAsync 2 {}" + Thread.currentThread().getName()))
                .thenAccept((v) -> LOGGER.info("runAsync 2 {}" + Thread.currentThread().getName()))
                .thenAcceptAsync((v) -> LOGGER.info("runAsync 2 {}" + Thread.currentThread().getName()), delayedExecutor)
                .thenAccept((v) -> LOGGER.info("runAsync 2 {}" + Thread.currentThread().getName()))
                .whenCompleteAsync(CompleteFutureAide.runComplete(latch::countDown));
        latch.await();
    }

    @Test
    void noopApply() {

    }

}