/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.data.cache;

import com.tny.game.common.concurrent.*;
import com.tny.game.common.concurrent.collection.*;
import org.slf4j.*;

import java.util.Set;
import java.util.concurrent.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/16 11:38 上午
 */
public class ScheduledCacheRecycler implements CacheRecycler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledCacheRecycler.class);

    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newScheduledThreadPool(1,
            new CoreThreadFactory("TimerCacheRecyclerScheduled", true));

    private long recycleIntervalTime = 15000;

    private final Set<RecyclableCache> caches = new ConcurrentHashSet<>();

    public ScheduledCacheRecycler() {
    }

    public ScheduledCacheRecycler(long recycleIntervalTime) {
        this.recycleIntervalTime = recycleIntervalTime;
    }

    @Override
    public void accept(RecyclableCache cache) {
        if (caches.add(cache)) {
            scheduled(cache, recycleIntervalTime + ThreadLocalRandom.current().nextLong(recycleIntervalTime));
        }
    }

    private void scheduled(RecyclableCache cache, long delay) {
        SCHEDULED_EXECUTOR_SERVICE.schedule(() -> {
            if (!caches.contains(cache)) {
                return;
            }
            try {
                cache.recycle();
            } catch (Throwable e) {
                LOGGER.error("recycle {} exception", cache, e);
            } finally {
                scheduled(cache, recycleIntervalTime);
            }
        }, delay, TimeUnit.MILLISECONDS);
    }

}
