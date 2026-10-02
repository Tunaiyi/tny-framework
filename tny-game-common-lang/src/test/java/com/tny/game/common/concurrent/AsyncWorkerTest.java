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
package com.tny.game.common.concurrent;

import com.tny.game.common.concurrent.worker.*;
import org.junit.jupiter.api.*;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.*;

import static com.tny.game.common.concurrent.worker.AbstractAsyncWorker.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/9/27 16:15
 **/
public class AsyncWorkerTest {

    private final SerialAsyncWorker executor = AsyncWorker.createSerialWorker("AsyncWorkerTest", ForkJoinPools.pool("AsyncWorkerTest"));
    //    private final AsyncWorker executor = new AsyncSingleWorker(ForkJoinPools.commonPool());
    //    private final AsyncWorker executor = new SingleWorkerExecutor(ForkJoinPools.commonPool());

    @Test
    void test() throws ExecutionException, InterruptedException {
        executor.await(() -> {
            debug("1 - 1");
            return executor.await(() -> {
                        debug("2 - 1");
                        return executor.await(() -> {
                                    debug("3 - 1");
                                    return executor.awaitDelay(1000)
                                            .whenComplete((v, c) -> {
                                                executor.execute(() -> debug("4 - 1"));
                                                debug("3 - 2");
                                            })
                                            .thenCompose(v -> {
                                                var delay = CompletableFuture.delayedExecutor(1000, TimeUnit.MILLISECONDS);
                                                return CompletableFuture.runAsync(() -> debug("5 - 1"), delay);
                                            })
                                            .thenCompose((v) -> {
                                                System.out.println("6 - 1 ====== " + Thread.currentThread());
                                                return executor.run(() -> debug("6 - 1"));
                                            });
                                })
                                .whenComplete((v, c) -> debug("2 - 2"));
                    })
                    .whenComplete((v, c) -> debug("1 - 2"));
        }).get();
    }

    private static void debug(String message) {
        var thread = Thread.currentThread();
        System.out.println(
                DateTimeFormatter.ISO_INSTANT.format(Instant.now()) + " # " + message + "|" + current() + "|" + thread.getName() + "-" +
                thread.getId());
    }

}
