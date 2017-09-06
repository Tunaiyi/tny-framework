package com.tny.game.net.message;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.session.MessageFuture;
import com.tny.game.net.tunnel.Tunnel;

import java.rmi.server.UID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Future;

/**
 * Created by Kun Yang on 2017/2/16.
 */
public class MessageContent<R> implements Protocol {

    private static final long SEND_TIMEOUT = 5000;
    private static final long RESPONSE_TIMEOUT = 10000;

    private ResultCode code = ResultCode.SUCCESS;

    private int protocol;

    private Object body;

    private int toMessage;

    private MessageMode mode;

    private volatile CompletableFuture<Tunnel<UID>> sendFuture;

    private volatile MessageFuture<R> messageFuture;

    private long sentTimeout = SEND_TIMEOUT;

    public static <O> MessageContent<O> toPush(Protocol protocol, ResultCode code, Object body) {
        return new MessageContent<>(protocol, code, body, -1);
    }

    public static <O> MessageContent<O> toPush(Protocol protocol, ResultCode code) {
        return new MessageContent<>(protocol, code, null, -1);
    }

    public static <O> MessageContent<O> toPush(Protocol protocol, Object body) {
        return new MessageContent<>(protocol, ResultCode.SUCCESS, body, -1);
    }

    public static <O> MessageContent<O> toRequest(Protocol protocol, Object body) {
        return new MessageContent<>(protocol, ResultCode.SUCCESS, body, 0);
    }

    public static <O> MessageContent<O> toResponse(Protocol protocol, Object body, int toMessage) {
        return new MessageContent<>(protocol, ResultCode.SUCCESS, body, toMessage);
    }

    public static <O> MessageContent<O> toResponse(Protocol protocol, ResultCode code, Object body, int toMessage) {
        return new MessageContent<>(protocol, code, body, toMessage);
    }

    public static <O> MessageContent<O> toResponse(Protocol protocol, ResultCode code, int toMessage) {
        return new MessageContent<>(protocol, code, null, toMessage);
    }

    public static <O> MessageContent<O> toResponse(Message<?> message, ResultCode code) {
        return new MessageContent<>(message, code, null, message.getID());
    }

    private MessageContent() {
    }

    private MessageContent(Protocol protocol, ResultCode code, Object body, Integer toMessage) {
        this.protocol = protocol.getProtocol();
        this.code = code;
        this.body = body;
        this.toMessage = toMessage;
        this.mode = MessageMode.getMode(toMessage);
    }

    public ResultCode getCode() {
        return code;
    }

    public Object getBody() {
        return body;
    }

    public int getToMessage() {
        return toMessage;
    }

    private CompletableFuture<Tunnel<UID>> createSentFuture() {
        if (this.sendFuture != null)
            return this.sendFuture;
        synchronized (this) {
            if (this.sendFuture == null)
                this.sendFuture = new CompletableFuture<>();
        }
        return this.sendFuture;
    }

    public CompletionStage<Tunnel<UID>> sendCompletionStage() {
        return createSentFuture();
    }

    public Future<Tunnel<UID>> sendFuture() {
        return createSentFuture();
    }

    public Future<Tunnel<UID>> getSendFailure() {
        return this.sendFuture;
    }

    public MessageContent<R> setWaitForSent() {
        this.sentTimeout = SEND_TIMEOUT;
        return this;
    }

    public MessageContent<R> setWaitForSent(long sentTimeout) {
        this.sentTimeout = sentTimeout;
        createSentFuture();
        return this;
    }

    public MessageFuture<R> createMessageFuture() {
        return createMessageFuture(RESPONSE_TIMEOUT);
    }

    public MessageFuture<R> createMessageFuture(long timeout) {
        if (MessageAide.isRequest(this.toMessage)) {
            if (this.messageFuture != null)
                return this.messageFuture;
            synchronized (this) {
                if (this.messageFuture != null)
                    return this.messageFuture;
                if (timeout > 0)
                    this.messageFuture = new MessageFuture<>(timeout);
                else
                    this.messageFuture = new MessageFuture<>();
            }
        }
        return this.messageFuture;
    }

    public MessageFuture<R> getMessageFuture() {
        return messageFuture;
    }

    public boolean hasMessageFuture() {
        return this.messageFuture != null;
    }

    // @SuppressWarnings("unchecked")
    // public <U> Tunnel<U> getTunnel() {
    //     return (Tunnel<U>) this.tunnel;
    // }

    @Override
    public int getProtocol() {
        return protocol;
    }

    public long getSentTimeout() {
        return sentTimeout;
    }

    public boolean isWaitForSent() {
        return sentTimeout >= 0;
    }

    public MessageMode getMode() {
        return this.mode;
    }

}
