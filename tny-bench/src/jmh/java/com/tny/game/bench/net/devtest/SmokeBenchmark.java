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
package com.tny.game.bench.net.devtest;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * 冒烟基准：验证 BenchmarkList 生成与 bench 任务执行链（optimize-net-hot-path 任务 1.3）。
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
public class SmokeBenchmark {

    private int base;

    @Setup
    public void setUp() {
        base = 41;
    }

    @Benchmark
    public int noop() {
        return base + 1;
    }

}
