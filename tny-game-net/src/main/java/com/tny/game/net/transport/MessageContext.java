package com.tny.game.net.transport;

import com.google.common.base.MoreObjects;
import com.tny.game.common.concurrent.StageableFuture;
import com.tny.game.common.result.ResultCode;
import com.tny.game.common.utils.Throws;
import com.tny.game.net.transport.message.*;

import java.util.function.Consumer;

import static com.tny.game.net.transport.message.MessageAide.*;

/**
 * Created by Kun Yang on 2017/2/16.
 */
public class MessageContext<UID> implements Protocol {

    private static final long DEFAULT_MESSAGE_TIMEOUT = 10000;
    private static final long RESPONSE_TIMEOUT = 6000;

    private ResultCode code = ResultCode.SUCCESS;

    private int protocol;

    private Object body;

    private long toMessage;

    private MessageMode mode;

    /**
     * 发送消息 Future
     */
    private volatile CommonStageableFuture<Message<UID>> sendFuture;

    /**
     * 收到响应消息 Future, 只有 mode 为  request 才可以是使用
     */
    private volatile RespondFuture<UID> respondFuture;

    public static <O> MessageContext<O> toPush(Protocol protocol, ResultCode code, Object body) {
        return new MessageContext<>(protocol, code, body, PUSH_TO_MESSAGE_MAX_ID);
    }

    public static <O> MessageContext<O> toPush(Protocol protocol, ResultCode code) {
        return new MessageContext<>(protocol, code, null, PUSH_TO_MESSAGE_MAX_ID);
    }

    public static <O> MessageContext<O> toPush(Protocol protocol, Object body) {
        return new MessageContext<>(protocol, ResultCode.SUCCESS, body, PUSH_TO_MESSAGE_MAX_ID);
    }

    public static <O> MessageContext<O> toRequest(Protocol protocol, Object body) {
        return new MessageContext<>(protocol, ResultCode.SUCCESS, body, REQUEST_TO_MESSAGE_ID);
    }

    public static <O> MessageContext<O> toResponse(Protocol protocol, Object body, long toMessage) {
        return new MessageContext<>(protocol, ResultCode.SUCCESS, body, toMessage);
    }

    public static <O> MessageContext<O> toResponse(Protocol protocol, ResultCode code, Object body, long toMessage) {
        return new MessageContext<>(protocol, code, body, toMessage);
    }

    public static <O> MessageContext<O> toResponse(Protocol protocol, ResultCode code, long toMessage) {
        return new MessageContext<>(protocol, code, null, toMessage);
    }

    public static <O> MessageContext<O> toResponse(MessageHeader header, ResultCode code, Object body) {
        return new MessageContext<>(header, code, body, header.getId());
    }

    public static <O> MessageContext<O> toResponse(Message<?> message, ResultCode code, Object body) {
        return toResponse(message.getHeader(), code, body);
    }

    public static <O> MessageContext<O> toResponse(MessageHeader header, ResultCode code) {
        return toResponse(header, code, null, header.getId());
    }

    public static <O> MessageContext<O> toResponse(Message<?> message, ResultCode code) {
        return toResponse(message.getHeader(), code);
    }

    private MessageContext() {
    }

    private MessageContext(Protocol protocol, ResultCode code, Object body, Long toMessage) {
        this.protocol = protocol.getProtocol();
        this.code = code;
        this.body = body;
        this.toMessage = toMessage;
        this.mode = MessageMode.getMode(toMessage);
    }

    @Override
    public int getProtocol() {
        return protocol;
    }

    public ResultCode getCode() {
        return code;
    }

    public Object getBody() {
        return body;
    }

    public MessageMode getMode() {
        return this.mode;
    }

    public long getToMessage() {
        return toMessage;
    }

    public MessageContext<UID> willSendFuture(Consumer<StageableFuture<Message<UID>>> consumer) {
        consumer.accept(this.willResponseFuture());
        return this;
    }

    public StageableFuture<Message<UID>> willSendFuture() {
        if (this.sendFuture != null)
            return this.sendFuture;
        synchronized (this) {
            if (this.sendFuture == null)
                this.sendFuture = CommonStageableFuture.createFuture();
        }
        return this.sendFuture;
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

    public StageableFuture<Message<UID>> willResponseFuture() {
        return willResponseFuture(RESPONSE_TIMEOUT);
    }

    public MessageContext<UID> willResponseFuture(Consumer<StageableFuture<Message<UID>>> consumer) {
        consumer.accept(this.willResponseFuture());
        return this;
    }

    public StageableFuture<Message<UID>> willResponseFuture(long timeout) {
        Throws.checkArgument(MessageAide.isRequest(this.toMessage), "请求模式非 {}, 无法创建MessageFuture", MessageMode.REQUEST);
        if (this.respondFuture != null)
            return this.respondFuture;
        synchronized (this) {
            if (this.respondFuture != null)
                return this.respondFuture;
            if (timeout > 0)
                this.respondFuture = new RespondFuture<>(timeout);
            else
                this.respondFuture = new RespondFuture<>(DEFAULT_MESSAGE_TIMEOUT);
        }
        return this.respondFuture;
    }

    void cancelSend(boolean mayInterruptIfRunning) {
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

    void sendSuccess(NetTunnel<UID> tunnel, Message<UID> message) {
        if (this.sendFuture != null) {
            CommonStageableFuture<Message<UID>> sendFuture = this.sendFuture;
            if (!sendFuture.isDone()) {
                sendFuture.future().complete(message);
            }
        }
        if (this.respondFuture != null && tunnel != null) {
            tunnel.registerFuture(message.getId(), this.respondFuture);
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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("protocol", protocol)
                .add("mode", mode)
                .add("toMessage", toMessage)
                .add("code", code)
                .add("body", body)
                .toString();
    }

}
