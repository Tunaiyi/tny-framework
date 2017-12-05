package com.tny.game.net.message;

import com.tny.game.common.result.ResultCode;
import com.tny.game.suite.base.Throws;
import com.tny.game.net.session.MessageFuture;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.concurrent.CompletableFuture;

/**
 * Created by Kun Yang on 2017/2/16.
 */
public class MessageContent<UID> implements Protocol {

    private static final long SEND_TIMEOUT = 0;
    private static final long RESPONSE_TIMEOUT = 6000;

    private ResultCode code = ResultCode.SUCCESS;

    private int protocol;

    private Object body;

    private int toMessage;

    private MessageMode mode;

    private volatile CompletableFuture<Message<UID>> sendFuture;

    private volatile MessageFuture<UID> messageFuture;

    private long waitSendTimeout = -1;

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

    public CompletableFuture<Message<UID>> sendFuture() {
        if (this.sendFuture != null)
            return this.sendFuture;
        synchronized (this) {
            if (this.sendFuture == null)
                this.sendFuture = new CompletableFuture<>();
        }
        return this.sendFuture;
    }

    public CompletableFuture<Message<UID>> messageFuture() {
        return messageFuture(RESPONSE_TIMEOUT);
    }

    public CompletableFuture<Message<UID>> messageFuture(long timeout) {
        Throws.checkArgument(MessageAide.isRequest(this.toMessage), "请求模式非 {}, 无法创建MessageFuture", MessageMode.REQUEST);
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
        return this.messageFuture;
    }

    public MessageContent<UID> waitForSent() {
        return this.waitForSent(SEND_TIMEOUT);
    }

    public MessageContent<UID> waitForSent(long sentTimeout) {
        this.waitSendTimeout = sentTimeout;
        this.sendFuture = sendFuture();
        return this;
    }

    @Override
    public int getProtocol() {
        return protocol;
    }

    public long getWaitForSentTimeout() {
        return waitSendTimeout;
    }

    public boolean isWaitForSent() {
        return waitSendTimeout > 0;
    }

    public MessageMode getMode() {
        return this.mode;
    }

    void cancelSendWait(boolean mayInterruptIfRunning) {
        if (this.sendFuture != null) {
            CompletableFuture<Message<UID>> sendFuture = this.sendFuture;
            if (!sendFuture.isDone()) {
                sendFuture.cancel(mayInterruptIfRunning);
            }
        }
        if (this.messageFuture != null) {
            CompletableFuture<Message<UID>> messageFuture = this.sendFuture;
            if (!messageFuture.isDone()) {
                messageFuture.cancel(mayInterruptIfRunning);
            }
        }
    }

    void sendSuccess(Message<UID> message) {
        if (this.sendFuture != null) {
            CompletableFuture<Message<UID>> sendFuture = this.sendFuture;
            if (!sendFuture.isDone()) {
                sendFuture.complete(message);
            }
        }
        if (this.messageFuture != null) {
            MessageFuture.putFuture(message, this.messageFuture);
        }
    }

    void sendFailed(Throwable cause) {
        if (this.sendFuture != null) {
            CompletableFuture<Message<UID>> sendFuture = this.sendFuture;
            if (!sendFuture.isDone()) {
                sendFuture.completeExceptionally(cause);
            }
        }
        if (this.messageFuture != null) {
            CompletableFuture<Message<UID>> messageFuture = this.messageFuture;
            if (!messageFuture.isDone()) {
                messageFuture.completeExceptionally(cause);
            }
        }
    }

    public boolean isHasFuture() {
        return this.sendFuture != null || this.messageFuture != null;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("protocol", protocol)
                .append("mode", mode)
                .append("toMessage", toMessage)
                .append("code", code)
                .append("body", body)
                .toString();
    }
}
