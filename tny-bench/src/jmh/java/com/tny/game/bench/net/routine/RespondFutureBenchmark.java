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
