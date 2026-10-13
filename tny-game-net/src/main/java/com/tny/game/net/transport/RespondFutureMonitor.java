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
package com.tny.game.net.transport;

import com.google.common.collect.*;
import com.tny.game.common.concurrent.*;
import org.slf4j.*;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2018-09-18 10:58
 */
public class RespondFutureMonitor {

    private static final long INIT_DELAY = 10;

    private static final long PERIOD = 5;

    private static final ConcurrentMap<Object, RespondFutureMonitor> FUTURE_HOLDER_MAP = new MapMaker()
            .concurrencyLevel(32)
            .weakKeys()
            .makeMap();

    public static final Logger LOGGER = LoggerFactory.getLogger(RespondFutureMonitor.class);

    private volatile boolean close = false;

    private final Lock lock = new ReentrantLock();

    static {
        Executors.newSingleThreadScheduledExecutor(new CoreThreadFactory("SessionEventBoxCleaner", true))
                .scheduleAtFixedRate(RespondFutureMonitor::clearTimeoutFuture, INIT_DELAY, PERIOD, TimeUnit.SECONDS);
    }

    public static RespondFutureMonitor getHolder(Object object) {
        return FUTURE_HOLDER_MAP.computeIfAbsent(object, k -> new RespondFutureMonitor());
    }

    public static void removeHolder(Object object) {
        RespondFutureMonitor holder = FUTURE_HOLDER_MAP.remove(object);
        if (holder == null) {
            return;
        }
        holder.close();
    }

    private static void clearTimeoutFuture() {
        for (Entry<Object, RespondFutureMonitor> entry : FUTURE_HOLDER_MAP.entrySet()) {
            try {
                RespondFutureMonitor holder = entry.getValue();
                holder.clearTimeOut();
            } catch (Throwable e) {
                LOGGER.error("", e);
            }
        }
    }

    private volatile ConcurrentMap<Long, MessageRespondFuture> futureMap;

    private void clearTimeOut() {
        ConcurrentMap<Long, MessageRespondFuture> futureMap = this.futureMap;
        if (futureMap == null) {
            return;
        }
        for (Entry<Long, MessageRespondFuture> entry : futureMap.entrySet()) {
            try {
                MessageRespondFuture future = entry.getValue();
                if (future.isDone() || (future.isTimeout() && future.cancel(true))) {
                    futureMap.remove(entry.getKey());
                }
            } catch (Throwable e) {
                LOGGER.error("", e);
            }
        }
    }

    public void close() {
        if (this.close) {
            return;
        }
        this.close = true;
        ConcurrentMap<Long, MessageRespondFuture> futureMap = this.futureMap;
        if (futureMap == null) {
            return;
        }
        List<MessageRespondFuture> futures;
        if (this.futureMap.isEmpty()) {
            futures = ImmutableList.of();
        } else {
            futures = new ArrayList<>(this.futureMap.values());
            this.futureMap.clear();
        }
        for (MessageRespondFuture future : futures) {
            try {
                future.cancel(true);
            } catch (Throwable e) {
                LOGGER.error("", e);
            }
        }
    }

    private ConcurrentMap<Long, MessageRespondFuture> map() {
        if (this.futureMap != null) {
            return this.futureMap;
        }

        lock.lock();
        try {
            if (this.futureMap != null) {
                return this.futureMap;
            }
            this.futureMap = new ConcurrentHashMap<>();
        } finally {
            lock.unlock();
        }
        return this.futureMap;
    }

    public <M> MessageRespondFuture getFuture(long messageId) {
        ConcurrentMap<Long, MessageRespondFuture> map = this.futureMap;
        if (map == null) {
            return null;
        }
        return as(map.get(messageId));
    }

    public <M> MessageRespondFuture pollFuture(long messageId) {
        ConcurrentMap<Long, MessageRespondFuture> map = this.futureMap;
        if (map == null) {
            return null;
        }
        return as(map.remove(messageId));
    }

    public void putFuture(long messageId, MessageRespondFuture future) {
        if (future == null) {
            return;
        }
        if (!this.close) {
            ConcurrentMap<Long, MessageRespondFuture> map = map();
            MessageRespondFuture oldFuture = map.put(messageId, future);
            if (oldFuture != null && !oldFuture.isDone()) {
                oldFuture.cancel(true);
            }
        } else {
            future.cancel(true);
        }
    }

    public int size() {
        ConcurrentMap<Long, MessageRespondFuture> map = this.futureMap;
        if (map == null) {
            return 0;
        }
        return map.size();
    }

}