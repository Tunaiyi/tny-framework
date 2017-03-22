package com.tny.game.net.session;

import com.tny.game.common.concurrent.AbstractFuture;
import com.tny.game.common.result.ResultCodes;
import com.tny.game.net.base.NetLogger;
import com.tny.game.net.base.CoreResponseCode;
import com.tny.game.net.message.Message;
import com.tny.game.net.message.MessageAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

public class MessageFuture<M> extends AbstractFuture<Message<?>> {

    protected static final Logger LOG = LoggerFactory.getLogger(NetLogger.NIO_CLIENT);

    private Message<?> request;

    private long timeout;

    private NetSession session;

    private MessageAction<Object> responseAction;

    private long createAt = System.currentTimeMillis();

    public MessageFuture(MessageAction<?> responseAction) {
        this(responseAction, 30000L);
    }

    public MessageFuture(MessageAction<?> responseAction, long timeout) {
        this.setResponseAction(responseAction);
        if (timeout < 0)
            timeout = 30000L;
        this.timeout = System.currentTimeMillis() + timeout;
    }

    protected long getCreateAt() {
        return this.createAt;
    }

    public MessageFuture<?> setSession(NetSession session) {
        this.session = session;
        return this;
    }

    public MessageFuture<?> setRequest(Message<?> message) {
        this.request = message;
        return this;
    }

    @SuppressWarnings("unchecked")
    protected MessageFuture<?> setResponseAction(MessageAction<?> responseAction) {
        if (responseAction != null)
            this.responseAction = (MessageAction<Object>) responseAction;
        return this;
    }

    public int getRequestID() {
        return this.request.getID();
    }

    public boolean isTimeout() {
        return System.currentTimeMillis() >= timeout;
    }

    public void setResponse(Message message) {
        this.set(message);
        if (this.isHasAction()) {
            try {
                Object body = message.getBody(Object.class);
                this.responseAction.handle(this.session, this.request, ResultCodes.of(message.getCode()), body);
            } catch (Throwable e) {
                LOG.error("call request [{}]'s response [{}] action {}", request, message, this.responseAction.getClass(), e);
            }
        }
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        boolean cancel = super.cancel(mayInterruptIfRunning);
        if (cancel) {
            session.removeFuture(this);
            try {
                this.responseAction.handle(this.session, this.request, CoreResponseCode.REQUEST_FAILED, null);
            } catch (Throwable e) {
                LOG.error("cancel request [{}] then call action {} exception", request, this.responseAction.getClass(), e);
            }
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
