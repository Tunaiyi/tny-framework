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
package com.tny.game.bench.net.routine;

import com.tny.game.net.transport.*;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * RPC future 配对成本画像（任务 2.3）：在册 future 规模对 put/poll 的影响
 * （为 RespondFutureMonitor 全量扫描优化供决策数据；后台 5s 定时器为已知数据污染源，见基线报告）。
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 10, time = 1)
public class RespondFutureBenchmark {

    @Param({"1000", "10000"})
    private int inflight;

    private final AtomicLong idGenerator = new AtomicLong();
    private Object holderKey;
    private RespondFutureMonitor monitor;

    @Setup(Level.Trial)
    public void setUp() {
        idGenerator.set(inflight);
        holderKey = new Object();
        monitor = RespondFutureMonitor.getHolder(holderKey);
        for (long i = 0; i < inflight; i++) {
            monitor.putFuture(i, new MessageRespondFuture());
        }
    }

    @Benchmark
    public void putAndPoll() {
        long id = idGenerator.incrementAndGet();
        MessageRespondFuture future = new MessageRespondFuture();
        monitor.putFuture(id, future);
        monitor.pollFuture(id);
    }

    @Benchmark
    public Object pollMiss() {
        // 永不命中的键空间（inflight 之后单调递增），测量 miss 路径成本
        return monitor.pollFuture(inflight + idGenerator.incrementAndGet());
    }

}
