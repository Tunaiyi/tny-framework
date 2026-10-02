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

package com.tny.game.net.command.dispatcher;

import com.tny.game.net.application.*;

import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/9 4:09 下午
 */
public class AutoIncrementIdGenerator implements NetIdGenerator {

    private static final int PROCESSORS_SIZE = Runtime.getRuntime().availableProcessors();

    private final AtomicLong[] idGenerators;

    private final int bitSize;

    public AutoIncrementIdGenerator() {
        this(PROCESSORS_SIZE);
    }

    public AutoIncrementIdGenerator(int concurrentLevel) {
        // index 需要 ceil(log2(n)) 位；bitCount(n) 仅在 n=2 或 n-1 全 1 时巧合相等，
        // 其余取值会让 index 侵占计数位导致跨分片撞号（消息 ID 冲突 → 请求响应错配）
        this.bitSize = 32 - Integer.numberOfLeadingZeros(concurrentLevel - 1);
        this.idGenerators = new AtomicLong[concurrentLevel];
        for (int i = 0; i < idGenerators.length; i++) {
            idGenerators[i] = new AtomicLong();
        }
    }

    @Override
    public long generate() {
        long id = Thread.currentThread().getId();
        int index = (int) Math.floorMod(id, idGenerators.length);
        AtomicLong generator = idGenerators[index];
        return generator.incrementAndGet() << bitSize | index;
    }

}
