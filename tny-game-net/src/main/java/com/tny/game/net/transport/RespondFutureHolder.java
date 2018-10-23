package com.tny.game.net.transport;

import com.google.common.collect.*;
import com.tny.game.common.concurrent.CoreThreadFactory;
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
public class RespondFutureHolder {

    private static final long INIT_DELAY = 20;
    private static final long PERIOD = 20;
    private static final ConcurrentMap<Object, RespondFutureHolder> FUTURE_HOLDER_MAP = new MapMaker()
            .concurrencyLevel(32)
            .weakKeys()
            .makeMap();

    public static final Logger LOGGER = LoggerFactory.getLogger(RespondFutureHolder.class);

    private volatile boolean close = false;

    static {
        Executors.newSingleThreadScheduledExecutor(new CoreThreadFactory("SessionEventBoxCleaner", true))
                .scheduleAtFixedRate(RespondFutureHolder::clearTimeoutFuture, INIT_DELAY, PERIOD, TimeUnit.SECONDS);
    }

    public static RespondFutureHolder getHolder(Object object) {
        return FUTURE_HOLDER_MAP.computeIfAbsent(object, k -> new RespondFutureHolder());
    }

    public static void removeHolder(Object object) {
        RespondFutureHolder holder = FUTURE_HOLDER_MAP.remove(object);
        if (holder == null)
            return;
        holder.close();

    }

    private static void clearTimeoutFuture() {
        for (Entry<Object, RespondFutureHolder> entry : FUTURE_HOLDER_MAP.entrySet()) {
            try {
                RespondFutureHolder holder = entry.getValue();
                holder.clearTimeOut();
            } catch (Throwable e) {
                LOGGER.error("", e);
            }
        }
    }

    private volatile ConcurrentMap<Long, RespondFuture<?>> futureMap;

    public void clearTimeOut() {
        ConcurrentMap<Long, RespondFuture<?>> futureMap = this.futureMap;
        if (futureMap == null)
            return;
        for (Entry<Long, RespondFuture<?>> entry : futureMap.entrySet()) {
            try {
                RespondFuture<?> future = entry.getValue();
                if (future.isDone() || (future.isTimeout() && future.cancel(true)))
                    futureMap.remove(entry.getKey());
            } catch (Throwable e) {
                LOGGER.error("", e);
            }
        }
    }

    public void close() {
        if (this.close)
            return;
        this.close = true;
        ConcurrentMap<Long, RespondFuture<?>> futureMap = this.futureMap;
        if (futureMap == null)
            return;
        List<RespondFuture<?>> futures;
        if (!this.futureMap.isEmpty()) {
            futures = ImmutableList.of();
        } else {
            futures = new ArrayList<>(this.futureMap.values());
            this.futureMap.clear();
        }
        for (RespondFuture<?> future : futures) {
            try {
                future.cancel(true);
            } catch (Throwable e) {
                LOGGER.error("", e);
            }
        }
    }

    private ConcurrentMap<Long, RespondFuture<?>> map() {
        if (futureMap != null)
            return futureMap;
        synchronized (this) {
            if (futureMap != null)
                return futureMap;
            this.futureMap = new ConcurrentHashMap<>();
        }
        return this.futureMap;
    }

    public void putFuture(long messageId, RespondFuture<?> future) {
        if (future == null)
            return;
        if (!close) {
            ConcurrentMap<Long, RespondFuture<?>> map = map();
            RespondFuture<?> oldFuture = map.put(messageId, future);
            if (oldFuture != null && !oldFuture.isDone())
                oldFuture.cancel();
        } else {
            future.cancel(true);
        }
    }

    public <M> RespondFuture<M> pollFuture(long messageId) {
        ConcurrentMap<Long, RespondFuture<?>> map = this.futureMap;
        if (map == null)
            return null;
        return as(map.remove(messageId));
    }

}