/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.netty4.processor.disruptor;

import com.lmax.disruptor.*;
import com.tny.game.common.collection.map.access.*;

import java.util.concurrent.TimeUnit;
import java.util.function.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/20 4:10 下午
 */
public enum DisruptorWaitStrategy {

    YIELDING(YieldingWaitStrategy::new),

    BUSY_SPIN(BusySpinWaitStrategy::new),

    SLEEPING((m) -> new SleepingWaitStrategy(m.getInt("retries", 200), m.getInt("sleepTimeNs", 100))),

    BLOCKING(BlockingWaitStrategy::new),

    TIMEOUT_BLOCKING((m) -> new TimeoutBlockingWaitStrategy(m.getLong("timeoutNs", 200), TimeUnit.NANOSECONDS)),

    LITE_BLOCKING(LiteBlockingWaitStrategy::new),

    LITE_TIMEOUT_BLOCKING((m) -> new LiteTimeoutBlockingWaitStrategy(m.getLong("timeoutNs", 200), TimeUnit.NANOSECONDS)),

    //
    ;

    private final Function<MapAccessor, WaitStrategy> creator;

    DisruptorWaitStrategy(Function<MapAccessor, WaitStrategy> creator) {
        this.creator = creator;
    }

    DisruptorWaitStrategy(Supplier<WaitStrategy> creator) {
        this.creator = (m) -> creator.get();
    }

    public WaitStrategy createStrategy(MapAccessor accessor) {
        return this.creator.apply(accessor);
    }

}
