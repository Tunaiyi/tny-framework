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
package com.tny.game.net.transport;

import com.google.common.collect.*;
import com.tny.game.common.concurrent.*;
import org.slf4j.*;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-09-18 10:58
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
        synchronized (this) {
            if (this.futureMap != null) {
                return this.futureMap;
            }
            this.futureMap = new ConcurrentHashMap<>();
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