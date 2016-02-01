package com.tny.game.net.dispatcher;

import com.tny.game.common.concurrent.AbstractFuture;
import com.tny.game.log.CoreLogger;
import com.tny.game.net.base.CoreResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

public class MessageFuture<M> extends AbstractFuture<Response> {

    protected static final Logger LOG = LoggerFactory.getLogger(CoreLogger.NIO_CLIENT);

    private int requestID;

    private Session session;

    private Response response;

    private MessageAction<Object> responseAction;

    private long createAt = System.currentTimeMillis();

    public MessageFuture() {
    }

    protected long getCreateAt() {
        return this.createAt;
    }

    protected MessageFuture<?> setSession(Session session) {
        this.session = session;
        return this;
    }

    protected MessageFuture<?> setRequestID(Request request) {
        this.requestID = request.getID();
        return this;
    }

    @SuppressWarnings("unchecked")
    protected MessageFuture<?> setResponseAction(MessageAction<?> responseAction) {
        this.responseAction = (MessageAction<Object>) responseAction;
        return this;
    }

    public int getRequestID() {
        return this.requestID;
    }

    protected void setResponse(Response response) {
        this.set(response);
        if (this.isHasAction()) {
            try {
                Object body = this.response.getBody(Object.class);
                this.responseAction.handle(this.session, this.response.getResult(), body);
            } catch (Exception e) {
                LOG.error("call {} response action {}", this.response, this.responseAction.getClass(), e);
            }
        }
    }

    public boolean isHasAction() {
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
