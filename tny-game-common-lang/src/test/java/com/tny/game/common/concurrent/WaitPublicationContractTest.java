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

import org.junit.jupiter.api.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 等待原语发布契约（futures-executor-contracts ·「等待原语的终态判定跨线程立即可见且发布完整」）：
 * 终态不得因状态缓存跨线程永久粘滞；判定先行、取值在后必须读到完成方发布的最终值
 * （成功判定⇒结果完整，失败判定⇒原因非空），双读者成对观察同一终态。
 * 本类钉住 FutureWait 的 state volatile 发布序（先写 value/cause、后写 state）。
 */
class WaitPublicationContractTest {

    private static final long OBSERVE_DEADLINE_MILLIS = 5_000L;

    /** 有界自旋观察终态：超界即"状态粘滞"（无跨线程可见性） */
    private static void awaitTerminal(Wait<?> wait, java.util.function.Predicate<Wait<?>> terminal, String what) {
        long deadline = System.currentTimeMillis() + OBSERVE_DEADLINE_MILLIS;
        while (!terminal.test(wait)) {
            if (System.currentTimeMillis() > deadline) {
                fail("终态跨线程不可见（永久判为执行中）: " + what);
            }
            Thread.onSpinWait();
        }
    }

    /** 成功发布：两读者各自判定"已成功"后读到的值均与完成方写入值一致，且判定稳定保持 */
    @Test
    void successIsPublishedWithValueCrossThread() throws Exception {
        ExecutorService pool = Executors.newSingleThreadExecutor();
        try {
            for (int round = 0; round < 100; round++) {
                String value = "v-" + round;
                CountDownLatch gate = new CountDownLatch(1);
                Wait<String> wait = Wait.of(pool.submit(() -> {
                    gate.await();
                    return value;
                }));
                AtomicReference<String> seenA = new AtomicReference<>();
                AtomicReference<String> seenB = new AtomicReference<>();
                CountDownLatch done = new CountDownLatch(2);
                Thread a = new Thread(() -> {
                    awaitTerminal(wait, Wait::isSuccess, "success-A");
                    seenA.set(wait.getResult());
                    done.countDown();
                }, "reader-A");
                Thread b = new Thread(() -> {
                    awaitTerminal(wait, Wait::isSuccess, "success-B");
                    seenB.set(wait.getResult());
                    done.countDown();
                }, "reader-B");
                a.start();
                b.start();
                Thread.sleep(5); // 让读者在 EXECUTE 态空转若干轮
                gate.countDown();
                assertTrue(done.await(OBSERVE_DEADLINE_MILLIS * 2, TimeUnit.SECONDS), "读者线程未在界内完成");
                a.join(2000);
                b.join(2000);
                assertEquals(value, seenA.get(), "读者A在判定已成功后读到缺失/半发布的值");
                assertEquals(value, seenB.get(), "读者B在判定已成功后读到缺失/半发布的值");
                assertTrue(wait.isSuccess(), "终态判定不得回摆");
                assertEquals(value, wait.getResult(), "后续读取值不得漂移");
            }
        } finally {
            pool.shutdownNow();
        }
    }

    /** 失败发布：判定"已失败"后读到的原因非空且为完成方异常（经 Future 包装可达原始异常） */
    @Test
    void failureIsPublishedWithCauseCrossThread() throws Exception {
        ExecutorService pool = Executors.newSingleThreadExecutor();
        try {
            for (int round = 0; round < 100; round++) {
                IllegalStateException boom = new IllegalStateException("boom-" + round);
                CountDownLatch gate = new CountDownLatch(1);
                Wait<Object> wait = Wait.of(pool.submit(() -> {
                    gate.await();
                    throw boom;
                }));
                AtomicReference<Throwable> seen = new AtomicReference<>();
                CountDownLatch done = new CountDownLatch(1);
                Thread reader = new Thread(() -> {
                    awaitTerminal(wait, Wait::isFailed, "failed");
                    seen.set(wait.getCause());
                    done.countDown();
                }, "failure-reader");
                reader.start();
                gate.countDown();
                assertTrue(done.await(OBSERVE_DEADLINE_MILLIS * 2, TimeUnit.SECONDS), "失败终态未在界内可见");
                reader.join(2000);
                assertNotNull(seen.get(), "失败判定成立但原因为空（半发布）");
                Throwable unwrapped = seen.get() instanceof ExecutionException ? seen.get().getCause() : seen.get();
                assertSame(boom, unwrapped, "失败原因非完成方设置的异常");
            }
        } finally {
            pool.shutdownNow();
        }
    }

}
