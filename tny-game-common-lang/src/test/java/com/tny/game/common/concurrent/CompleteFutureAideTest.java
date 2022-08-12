/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

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