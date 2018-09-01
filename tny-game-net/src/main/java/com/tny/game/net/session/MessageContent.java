package com.tny.game.net.session;

import com.tny.game.common.concurrent.StageableFuture;
import com.tny.game.common.result.ResultCode;
import com.tny.game.common.utils.Throws;
import com.tny.game.net.message.*;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by Kun Yang on 2017/2/16.
 */
public class MessageContent<UID> implements Protocol {

    private static final long DEFAULT_SEND_TIMEOUT = 3000;
    private static final long DEFAULT_MESSAGE_TIMEOUT = 10000;
    private static final long RESPONSE_TIMEOUT = 6000;

    private ResultCode code = ResultCode.SUCCESS;

    private int protocol;

    private Object body;

    private int toMessage;

    private MessageMode mode;

    /**
     * 发送消息 Future
     */
    private volatile CommonStageableFuture<Message<UID>> sendFuture;

    /**
     * 收到响应消息 Future, 只有 mode 为  request 才可以是使用
     */
    private volatile RespondMessageFuture<UID> respondFuture;

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

    public static <O> MessageContent<O> toResponse(MessageHeader header, ResultCode code, Object body) {
        return new MessageContent<>(header, code, body, header.getId());
    }

    public static <O> MessageContent<O> toResponse(Message<?> message, ResultCode code, Object body) {
        return toResponse(message.getHeader(), code, body);
    }

    public static <O> MessageContent<O> toResponse(MessageHeader header, ResultCode code) {
        return toResponse(header, code, null, header.getId());
    }

    public static <O> MessageContent<O> toResponse(Message<?> message, ResultCode code) {
        return toResponse(message.getHeader(), code);
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

    public StageableFuture<Message<UID>> sendFuture() {
        return this.getOrCreateSendFuture();
    }

    public boolean isHasSendFuture() {
        return this.sendFuture != null;
    }

    public boolean isHasMessageFuture() {
        return this.respondFuture != null;
    }

    public boolean isHasFuture() {
        return this.sendFuture != null || this.respondFuture != null;
    }

    public StageableFuture<Message<UID>> messageFuture() {
        return messageFuture(RESPONSE_TIMEOUT);
    }

    public StageableFuture<Message<UID>> messageFuture(long timeout) {
        Throws.checkArgument(MessageAide.isRequest(this.toMessage), "请求模式非 {}, 无法创建MessageFuture", MessageMode.REQUEST);
        if (this.respondFuture != null)
            return this.respondFuture;
        synchronized (this) {
            if (this.respondFuture != null)
                return this.respondFuture;
            if (timeout > 0)
                this.respondFuture = new RespondMessageFuture<>(timeout);
            else
                this.respondFuture = new RespondMessageFuture<>(DEFAULT_MESSAGE_TIMEOUT);
        }
        return this.respondFuture;
    }

    public MessageContent<UID> waitForSent() {
        return this.waitForSent(DEFAULT_SEND_TIMEOUT);
    }

    public MessageContent<UID> waitForSent(long sentTimeout) {
        this.waitSendTimeout = sentTimeout;
        this.sendFuture = getOrCreateSendFuture();
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
            CommonStageableFuture<Message<UID>> sendFuture = this.sendFuture;
            if (!sendFuture.isDone()) {
                sendFuture.cancel(mayInterruptIfRunning);
            }
        }
        if (this.respondFuture != null) {
            CommonStageableFuture<Message<UID>> messageFuture = this.sendFuture;
            if (!messageFuture.isDone()) {
                messageFuture.cancel(mayInterruptIfRunning);
            }
        }
    }

    void sendSuccess(Message<UID> message) {
        if (this.sendFuture != null) {
            CommonStageableFuture<Message<UID>> sendFuture = this.sendFuture;
            if (!sendFuture.isDone()) {
                sendFuture.future().complete(message);
            }
        }
        if (this.respondFuture != null) {
            RespondMessageFutures.putFuture(message, this.respondFuture);
        }
    }

    void sendFailed(Throwable cause) {
        if (this.sendFuture != null) {
            CommonStageableFuture<Message<UID>> sendFuture = this.sendFuture;
            if (!sendFuture.isDone()) {
                sendFuture.future().completeExceptionally(cause);
            }
        }
        if (this.respondFuture != null) {
            CommonStageableFuture<Message<UID>> messageFuture = this.respondFuture;
            if (!messageFuture.isDone()) {
                messageFuture.future().completeExceptionally(cause);
            }
        }
    }

    private CommonStageableFuture<Message<UID>> getOrCreateSendFuture() {
        if (this.sendFuture != null)
            return this.sendFuture;
        synchronized (this) {
            if (this.sendFuture == null)
                this.sendFuture = CommonStageableFuture.createFuture();
        }
        return this.sendFuture;
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
