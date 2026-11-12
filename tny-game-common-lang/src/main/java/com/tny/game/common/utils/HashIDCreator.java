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

package com.tny.game.common.utils;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Kun Yang on 16/6/6.
 */
public class HashIDCreator implements IdCreator {

    private final AtomicLong[] creators;

    private int indexOffset;

    /** 实例身份：号段偏移，杜绝同毫秒新建实例间号域重叠（rpc 双校验器同毫秒初始化形态） */
    private static final AtomicLong INSTANCE_ORDINAL = new AtomicLong();

    public HashIDCreator(int size) {
        this.creators = new AtomicLong[size];
        // 起值 = 时间基 + 实例号段偏移（<<32：单实例需 4.3e9 次自增才可能侵入邻段）
        long startAt = System.currentTimeMillis() + (INSTANCE_ORDINAL.incrementAndGet() << 32);
        for (int index = 0; index < this.creators.length; index++) {
            this.creators[index] = new AtomicLong(startAt);
        }
        int indexSize = String.valueOf(size).length();
        this.indexOffset = 1;
        for (int i = 0; i < indexSize; i++)
            this.indexOffset *= 10;
    }

    @Override
    public long createId() {
        // floorMod：原 Math.abs(MIN_VALUE % size) 仍为负 → 数组越界（首轮同族缺陷）
        int index = Math.floorMod(Thread.currentThread().hashCode(), this.creators.length);
        return this.creators[index].incrementAndGet() * this.indexOffset + index;
    }

    public String getHexId() {
        return Long.toHexString(createId());
    }

}
