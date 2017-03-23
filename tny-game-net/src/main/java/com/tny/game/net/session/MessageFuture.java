package com.tny.game.net.session;

import com.tny.game.common.concurrent.AbstractFuture;
import com.tny.game.common.result.ResultCodes;
import com.tny.game.net.base.CoreResponseCode;
import com.tny.game.net.base.NetLogger;
import com.tny.game.net.message.Message;
import com.tny.game.net.message.MessageAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class MessageFuture<M> extends AbstractFuture<Message<?>> {

    protected static final Logger LOG = LoggerFactory.getLogger(NetLogger.NIO_CLIENT);

    private static final ConcurrentMap<String, MessageFuture<?>> FUTURE_MAP = new ConcurrentHashMap<>();

    private static String key(long id, int messageID) {
        return "future:" + id + "_" + messageID;
    }

    public static void putFuture(Session<?> session, Message<?> message, MessageFuture<?> future) {
        FUTURE_MAP.put(key(session.getID(), message.getID()), future);
    }

    @SuppressWarnings("unchecked")
    public static <M> MessageFuture<M> getFuture(long sessionID, int messageID) {
        return (MessageFuture<M>) FUTURE_MAP.get(key(sessionID, messageID));
    }

    private Message<?> message;

    private long timeout;

    private NetSession session;

    private MessageAction<Object> responseAction;

    private long createAt = System.currentTimeMillis();

    private Executor executor;

    public MessageFuture(MessageAction responseAction) {
        this(responseAction, null);
    }

    public MessageFuture(MessageAction responseAction, long timeout) {
        this(responseAction, null, timeout);
    }

    public MessageFuture(MessageAction responseAction, Executor executor) {
        this(responseAction, executor, 30000L);
    }

    @SuppressWarnings("unchecked")
    public MessageFuture(MessageAction responseAction, Executor executor, long timeout) {
        if (timeout < 0)
            timeout = 30000L;
        this.responseAction = responseAction;
        this.executor = executor;
        this.timeout = System.currentTimeMillis() + timeout;
    }

    protected long getCreateAt() {
        return this.createAt;
    }

    public MessageFuture<?> setSession(NetSession session) {
        this.session = session;
        return this;
    }

    public int getRequestID() {
        return this.message.getID();
    }

    public boolean isTimeout() {
        return System.currentTimeMillis() >= timeout;
    }

    public void setResponse(Message message) {
        this.set(message);
        this.message = message;
        if (this.isHasAction()) {
            if (executor != null)
                executor.execute(() -> this.invokeSuccessAction(message));
            else
                this.invokeSuccessAction(message);
        }
    }

    private void invokeSuccessAction(Message message) {
        try {
            Object body = message.getBody(Object.class);
            this.responseAction.handle(this.session, this.message, ResultCodes.of(message.getCode()), body);
        } catch (Throwable e) {
            LOG.error("call request [{}]'s response [{}] action {}", message, message, this.responseAction.getClass(), e);
        }
    }

    private void invokeFailedAction() {
        try {
            this.responseAction.handle(this.session, this.message, CoreResponseCode.REQUEST_FAILED, null);
        } catch (Throwable e) {
            LOG.error("cancel request [{}] then call action {} exception", message, this.responseAction.getClass(), e);
        }
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        boolean cancel = super.cancel(mayInterruptIfRunning);
        if (cancel && this.isHasAction()) {
            if (executor != null)
                executor.execute(this::invokeFailedAction);
            else
                this.invokeFailedAction();
        }
        return cancel;
    }

    private boolean isHasAction() {
        return this.responseAction != null;
    }

    public int getResult() throws InterruptedException, ExecutionException {
        Message message = this.get();
        if (message == null)
            return CoreResponseCode.REMOTE_NO_RESPONSE.getCode();
        return message.getCode();
    }

    @SuppressWarnings("unchecked")
    public M getBody() throws InterruptedException, ExecutionException {
        Message message = this.get();
        if (message == null)
            return null;
        return (M) message.getBody(Object.class);
    }

}
