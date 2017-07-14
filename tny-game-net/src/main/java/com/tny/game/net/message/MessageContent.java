package com.tny.game.net.message;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.session.MessageFuture;
import com.tny.game.net.tunnel.Tunnel;

import java.rmi.server.UID;
import java.util.concurrent.CompletableFuture;

/**
 * Created by Kun Yang on 2017/2/16.
 */
public class MessageContent<R> implements Protocol {

    public static final long SEND_TIMEOUT = 30000;

    private ResultCode code = ResultCode.SUCCESS;

    private int protocol;

    private Object body;

    private int toMessage;

    private CompletableFuture<Tunnel<UID>> sendFuture;

    private MessageFuture<R> messageFuture;

    private long sentTimeout = -1;

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

    public CompletableFuture<Tunnel<UID>> getSendFuture() {
        return sendFuture;
    }

    public MessageFuture<R> getMessageFuture() {
        return messageFuture;
    }

    // public MessageContent<R> setTunnel(Tunnel<?> tunnel) {
    //     this.tunnel = tunnel;
    //     return this;
    // }

    public MessageContent<R> setWaitSent() {
        this.sentTimeout = SEND_TIMEOUT;
        return createSentFuture();
    }

    public MessageContent<R> setWaitSent(long sentTimeout) {
        this.sentTimeout = sentTimeout;
        return createSentFuture();
    }


    public MessageContent<R> createSentFuture() {
        if (this.sendFuture == null)
            this.sendFuture = new CompletableFuture<>();
        return this;
    }

    public MessageContent<R> createMessageFuture() {
        if (MessageAide.isRequest(this.toMessage)) {
            if (this.messageFuture == null)
                this.messageFuture = new MessageFuture<>();
        }
        return this;
    }

    public MessageContent<R> createMessageFuture(long timeout) {
        if (MessageAide.isRequest(this.toMessage)) {
            if (this.messageFuture == null)
                this.messageFuture = new MessageFuture<>(timeout);
        }
        return this;
    }

    public long getSentTimeout() {
        return sentTimeout;
    }

    public boolean isSent() {
        return sentTimeout >= 0;
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
}
