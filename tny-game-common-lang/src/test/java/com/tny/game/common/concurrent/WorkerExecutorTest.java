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
package com.tny.game.common.concurrent;

import com.tny.game.common.concurrent.worker.*;
import org.junit.jupiter.api.*;

import java.util.concurrent.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/9/27 16:15
 **/
public class WorkerExecutorTest {

    private WorkerExecutor executor = new SingleWorkerExecutor(ForkJoinPools.commonPool(), true);

    @Test
    void test() throws ExecutionException, InterruptedException {
        executor.await(() -> {
            debug("1 - 1");
            return executor.await(() -> {
                        debug("2 - 1");
                        return executor.await(() -> {
                                    debug("3 - 1");
                                    return executor.delay(1000)
                                            .whenComplete((v, c) -> {
                                                executor.execute(() -> debug("4 - 1"));
                                                debug("3 - 2");
                                            })
                                            .thenCompose(v -> {
                                                var delay = CompletableFuture.delayedExecutor(1000, TimeUnit.MILLISECONDS);
                                                return CompletableFuture.runAsync(() -> debug("5 - 1"), delay);
                                            })
                                            .thenCompose((v) -> executor.run(() -> debug("6 - 1")));
                                })
                                .whenComplete((v, c) -> debug("2 - 2"));
                    })
                    .whenComplete((v, c) -> debug("1 - 2"));
        }).get();
    }

    private static void debug(String message) {
        var thread = Thread.currentThread();
        System.out.println(message + "|" + SerialWorkerExecutor.currentExecutor() + "|" + thread.getName() + "-" + thread.getId());
    }

}
