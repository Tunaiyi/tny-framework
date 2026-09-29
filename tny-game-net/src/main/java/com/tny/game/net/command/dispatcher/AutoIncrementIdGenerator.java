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
