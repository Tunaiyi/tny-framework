package com.tny.game.net.message;

import com.tny.game.common.concurrent.CoreThreadFactory;
import com.tny.game.net.base.NetLogger;
import org.slf4j.*;

import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.*;

public class RespondMessageFutures {

    protected static final Logger LOG = LoggerFactory.getLogger(NetLogger.NIO_CLIENT);

    private static final ConcurrentMap<FutureKey, RespondMessageFuture<?>> FUTURE_MAP = new ConcurrentHashMap<>();

    private static final long INIT_DELAY = 20;
    private static final long PERIOD = 20;


    static {
        Executors.newSingleThreadScheduledExecutor(new CoreThreadFactory("SessionEventBoxCleaner", true))
                .scheduleAtFixedRate(RespondMessageFutures::clearTimeoutFuture, INIT_DELAY, PERIOD, TimeUnit.SECONDS);
    }

    private static void clearTimeoutFuture() {
        for (Entry<FutureKey, RespondMessageFuture<?>> entry : FUTURE_MAP.entrySet()) {
            try {
                RespondMessageFuture<?> future = entry.getValue();
                if (future.isDone() || (future.isTimeout() && future.cancel(true)))
                    FUTURE_MAP.remove(entry.getKey());
            } catch (Throwable e) {
                LOG.error("", e);
            }
        }
    }

    private static FutureKey key(long sessionId, int messageID) {
        return new FutureKey(sessionId, messageID);
    }

    private static FutureKey key(Message<?> message) {
        return key(message.getSessionID(), message.getHeader().getId());
    }

    public static void putFuture(Message<?> message, RespondMessageFuture<?> future) {
        FUTURE_MAP.put(key(message), future);
    }

    public static boolean existFuture(long sessionID, int messageID) {
        return FUTURE_MAP.containsKey(key(sessionID, messageID));
    }

    @SuppressWarnings("unchecked")
    public static <M> RespondMessageFuture<M> pollFuture(long sessionID, int messageID) {
        return (RespondMessageFuture<M>) FUTURE_MAP.remove(key(sessionID, messageID));
    }

    @SuppressWarnings("unchecked")
    public static <M> RespondMessageFuture<M> getFuture(long sessionID, int messageID) {
        return (RespondMessageFuture<M>) FUTURE_MAP.get(key(sessionID, messageID));
    }


    private static class FutureKey {

        private long sessionID;

        private int id;

        private FutureKey(long sessionID, int id) {
            this.sessionID = sessionID;
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof FutureKey)) return false;
            FutureKey that = (FutureKey) o;
            return sessionID == that.sessionID &&
                    id == that.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(sessionID, id);
        }

    }
}
