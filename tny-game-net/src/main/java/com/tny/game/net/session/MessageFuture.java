package com.tny.game.net.session;

import com.tny.game.common.thread.CoreThreadFactory;
import com.tny.game.suite.app.NetLogger;
import com.tny.game.net.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MessageFuture<UID> extends CompletableFuture<Message<UID>> {

    protected static final Logger LOG = LoggerFactory.getLogger(NetLogger.NIO_CLIENT);

    private static final ConcurrentMap<String, MessageFuture<?>> FUTURE_MAP = new ConcurrentHashMap<>();

    private static final long INIT_DELAY = 20;
    private static final long PERIOD = 20;

    static {
        Executors.newSingleThreadScheduledExecutor(new CoreThreadFactory("SessionEventBoxCleaner", true))
                .scheduleAtFixedRate(MessageFuture::clearTimeoutFuture, INIT_DELAY, PERIOD, TimeUnit.SECONDS);
    }

    private static void clearTimeoutFuture() {
        for (Entry<String, MessageFuture<?>> entry : FUTURE_MAP.entrySet()) {
            try {
                MessageFuture<?> future = entry.getValue();
                if (future.isDone() || (future.isTimeout() && future.cancel(true)))
                    FUTURE_MAP.remove(entry.getKey());
            } catch (Throwable e) {
                LOG.error("", e);
            }
        }
    }

    private static String key(long id, int messageID) {
        return "future:" + id + "_" + messageID;
    }

    private static String key(Message<?> message) {
        return key(message.getSessionID(), message.getID());
    }

    public static void putFuture(Message<?> message, MessageFuture<?> future) {
        FUTURE_MAP.put(key(message), future);
    }

    public static boolean existFuture(long sessionID, int messageID) {
        return FUTURE_MAP.containsKey(key(sessionID, messageID));
    }

    @SuppressWarnings("unchecked")
    public static <M> MessageFuture<M> pollFuture(long sessionID, int messageID) {
        return (MessageFuture<M>) FUTURE_MAP.remove(key(sessionID, messageID));
    }

    @SuppressWarnings("unchecked")
    public static <M> MessageFuture<M> getFuture(long sessionID, int messageID) {
        return (MessageFuture<M>) FUTURE_MAP.get(key(sessionID, messageID));
    }

    private long timeout;

    public MessageFuture() {
        this(-1);
    }

    public MessageFuture(long timeout) {
        if (timeout <= 0)
            timeout = 30000L;
        this.timeout = System.currentTimeMillis() + timeout;
    }

    public boolean isTimeout() {
        return System.currentTimeMillis() >= timeout;
    }

}
