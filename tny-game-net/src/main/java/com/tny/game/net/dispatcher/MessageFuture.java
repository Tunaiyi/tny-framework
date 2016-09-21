package com.tny.game.net.dispatcher;

import com.tny.game.common.concurrent.AbstractFuture;
import com.tny.game.common.result.ResultCodes;
import com.tny.game.log.CoreLogger;
import com.tny.game.net.base.CoreResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

public class MessageFuture<M> extends AbstractFuture<Response> {

    protected static final Logger LOG = LoggerFactory.getLogger(CoreLogger.NIO_CLIENT);

    private Request request;

    private long timeout;

    private ClientSession session;

    private MessageAction<Object> responseAction;

    private long createAt = System.currentTimeMillis();

    public MessageFuture(MessageAction<?> responseAction) {
        this(responseAction, 30000L);
    }

    public MessageFuture(MessageAction<?> responseAction, long timeout) {
        this.setResponseAction(responseAction);
        this.timeout = System.currentTimeMillis() + timeout;
    }

    protected long getCreateAt() {
        return this.createAt;
    }

    public MessageFuture<?> setSession(ClientSession session) {
        this.session = session;
        return this;
    }

    public MessageFuture<?> setRequest(Request request) {
        this.request = request;
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

    protected void setResponse(Response response) {
        this.set(response);
        if (this.isHasAction()) {
            try {
                Object body = response.getBody(Object.class);
                this.responseAction.handle(this.session, this.request, ResultCodes.of(response.getResult()), body);
            } catch (Throwable e) {
                LOG.error("call request [{}]'s response [{}] action {}", request, response, this.responseAction.getClass(), e);
            }
        }
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        boolean cancel = super.cancel(mayInterruptIfRunning);
        if (cancel) {
            session.takeFuture(this.request.getID());
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
        Response response = this.get();
        if (response == null)
            return CoreResponseCode.REMOTE_NO_RESPONSE.getCode();
        return response.getResult();
    }

    @SuppressWarnings("unchecked")
    public M getBody() throws InterruptedException, ExecutionException {
        Response response = this.get();
        if (response == null)
            return null;
        return (M) response.getBody(Object.class);
    }

}
