package com.tny.game.net.transport;

import com.google.common.base.MoreObjects;
import com.tny.game.common.collection.empty.*;
import com.tny.game.common.concurrent.*;
import com.tny.game.common.result.*;
import com.tny.game.common.type.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;

import java.util.*;

import static com.tny.game.net.message.MessageAide.*;

/**
 * 消息上下文
 * Created by Kun Yang on 2017/2/16.
 */
public class MessageContexts {

    /**
     * 创建推送消息上下文
     *
     * @param protocol 协议号
     * @return 创建的消息上下文
     */
    public static MessageContext push(Protocol protocol) {
        return push(protocol, ResultCode.SUCCESS);
    }

    /**
     * 创建推送消息上下文
     *
     * @param protocol 协议号
     * @param code     消息结果码
     * @return 创建的消息上下文
     */
    public static MessageContext push(Protocol protocol, ResultCode code) {
        DefaultMessageContext context = new DefaultMessageContext();
        context.init(MessageMode.PUSH, protocol, code);
        return context;
    }

    /**
     * 创建推送消息上下文
     *
     * @param protocol 协议号
     * @param body     消息体
     * @return 创建的消息上下文
     */
    public static MessageContext push(Protocol protocol, Object body) {
        DefaultMessageContext context = new DefaultMessageContext();
        context.init(MessageMode.PUSH, protocol, ResultCode.SUCCESS)
                .withBody(body);
        return context;
    }

    /**
     * 创建推送消息上下文
     *
     * @param protocol 协议号
     * @param code     消息结果码
     * @param body     消息体
     * @return 创建的消息上下文
     */
    public static MessageContext push(Protocol protocol, ResultCode code, Object body) {
        DefaultMessageContext context = new DefaultMessageContext();
        context.init(MessageMode.PUSH, protocol, code)
                .withBody(body);
        return context;
    }

    /**
     * 创建请求消息上下文
     *
     * @param protocol      协议号
     * @param requestParams 请求参数, Body 转为 List
     * @return 创建的消息上下文
     */
    public static RequestContext request(Protocol protocol, Object... requestParams) {
        DefaultMessageContext context = new DefaultMessageContext();
        context.init(MessageMode.REQUEST, protocol, ResultCode.SUCCESS)
                .withBody(new MessageParamList(requestParams));
        return context;
    }

    /**
     * 创建响应消息上下文
     *
     * @param protocol  协议号
     * @param toMessage 响应的请求消息Id
     * @return 创建的消息上下文
     */
    public static MessageContext respond(Protocol protocol, long toMessage) {
        return respond(protocol, ResultCode.SUCCESS, toMessage);
    }

    /**
     * 创建响应消息上下文
     *
     * @param protocol  协议号
     * @param code      消息结果码
     * @param toMessage 响应的请求消息Id
     * @return 创建的消息上下文
     */
    public static MessageContext respond(Protocol protocol, ResultCode code, long toMessage) {
        DefaultMessageContext context = new DefaultMessageContext();
        context.init(MessageMode.RESPONSE, protocol, code, toMessage);
        return context;
    }

    /**
     * 创建响应消息上下文
     *
     * @param protocol  协议号
     * @param body      消息体
     * @param toMessage 响应的请求消息Id
     * @return 创建的消息上下文
     */
    public static MessageContext respond(Protocol protocol, Object body, long toMessage) {
        DefaultMessageContext context = new DefaultMessageContext();
        context.init(MessageMode.RESPONSE, protocol, ResultCode.SUCCESS, toMessage)
                .withBody(body);
        return context;
    }

    /**
     * 创建响应消息上下文
     *
     * @param protocol  协议号
     * @param code      消息结果码
     * @param body      消息体
     * @param toMessage 响应的请求消息Id
     * @return 创建的消息上下文
     */
    public static MessageContext respond(Protocol protocol, ResultCode code, Object body, long toMessage) {
        DefaultMessageContext context = new DefaultMessageContext();
        context.init(MessageMode.RESPONSE, protocol, code, toMessage)
                .withBody(body);
        return context;
    }

    /**
     * 创建响应消息上下文
     *
     * @param code        消息结果码
     * @param respondHead 响应的请求消息
     * @return 创建的消息上下文
     */
    public static MessageContext respond(ResultCode code, MessageHead respondHead) {
        DefaultMessageContext context = new DefaultMessageContext();
        context.init(MessageMode.RESPONSE, respondHead, code, respondHead.getId());
        return context;
    }

    public static MessageContext copy(Message message) {
        DefaultMessageContext context = new DefaultMessageContext();
        return context.init(message.getMode(), message.getHead(), message.getCode(), message.getToMessage())
                .withBody(message.getBody())
                .withHeaders(message.getAllHeaders());
    }

    private static class DefaultMessageContext extends RequestContext {

        private int code;

        private int protocol;

        private int line;

        private long toMessage;

        private MessageMode mode;

        private Object body;

        /**
         * 收到响应消息 Future, 只有 mode 为  request 才可以是使用
         */
        private volatile MessageRespondAwaiter respondAwaiter;

        /**
         * 收到响应消息 Future, 只有 mode 为  request 才可以是使用
         */
        private volatile MessageWriteAwaiter writeAwaiter;

        private final Map<String, MessageHeader<?>> headers = new EmptyImmutableMap<>();

        private DefaultMessageContext() {
        }

        private DefaultMessageContext init(MessageMode mode, Protocol protocol, ResultCode code) {
            return this.init(mode, protocol, code, EMPTY_MESSAGE_ID);
        }

        private DefaultMessageContext init(MessageMode mode, Protocol protocol, ResultCode code, Long toMessage) {
            return this.init(mode, protocol, code.getCode(), toMessage);
        }

        private DefaultMessageContext init(MessageMode mode, Protocol protocol, int code, Long toMessage) {
            Asserts.checkNotNull(protocol, "protocol is null");
            Asserts.checkNotNull(code, "code is null");
            this.protocol = protocol.getProtocolId();
            this.line = protocol.getLine();
            this.code = code;
            this.toMessage = toMessage;
            this.mode = mode;
            return this;
        }

        @Override
        public int getProtocolId() {
            return this.protocol;
        }

        @Override
        public int getLine() {
            return this.line;
        }

        @Override
        public int getCode() {
            return this.code;
        }

        @Override
        public MessageContext withHeader(MessageHeader<?> header) {
            if (header != null) {
                this.headers.put(header.getKey(), header);
            }
            return this;
        }

        @Override
        public MessageContext withHeaders(Collection<MessageHeader<?>> headers) {
            if (headers != null) {
                headers.forEach(header -> this.headers.put(header.getKey(), header));
            }
            return this;
        }

        @Override
        public MessageContext withCode(ResultCode code) {
            this.code = code.getCode();
            return this;
        }

        @Override
        public MessageMode getMode() {
            return this.mode;
        }

        @Override
        public long getToMessage() {
            return this.toMessage;
        }

        @Override
        public boolean existBody() {
            return this.body != null;
        }

        @Override
        public Object getBody() {
            return this.body;
        }

        @Override
        public Map<String, MessageHeader<?>> getAllHeadersMap() {
            return new HashMap<>(this.headers);
        }

        @Override
        public <T> T bodyAs(Class<T> clazz) {
            return ObjectAide.convertTo(this.body, clazz);
        }

        @Override
        public <T> T bodyAs(ReferenceType<T> clazz) {
            return ObjectAide.convertTo(this.body, clazz);
        }

        @Override
        public RequestContext withBody(Object body) {
            if (this.body != null) {
                throw new IllegalArgumentException("body is exist");
            }
            this.body = body;
            return this;
        }

        @Override
        public void cancel(boolean mayInterruptIfRunning) {
            if (this.writeAwaiter != null && !this.writeAwaiter.isDone()) {
                this.writeAwaiter.cancel(true);
            }
            if (this.respondAwaiter != null && !this.respondAwaiter.isDone()) {
                this.respondAwaiter.cancel(true);
            }
        }

        @Override
        public void cancel(Throwable throwable) {
            if (this.writeAwaiter != null && !this.writeAwaiter.isDone()) {
                this.writeAwaiter.completeExceptionally(throwable);
            }
            if (this.respondAwaiter != null && !this.respondAwaiter.isDone()) {
                this.respondAwaiter.completeExceptionally(throwable);
            }
        }

        @Override
        public RequestContext willRespondAwaiter(long timeoutMills) {
            if (this.mode == MessageMode.REQUEST) {
                this.respondAwaiter = new MessageRespondAwaiter(timeoutMills);
            }
            return this;
        }

        @Override
        public RequestContext willWriteAwaiter() {
            this.writeAwaiter = new MessageWriteAwaiter();
            return this;
        }

        @Override
        public MessageWriteAwaiter getWriteAwaiter() {
            return this.writeAwaiter;
        }

        @Override
        public MessageRespondAwaiter getResponseAwaiter() {
            return this.respondAwaiter;
        }

        @Override
        public boolean isRespondAwaitable() {
            return this.respondAwaiter != null;
        }

        @Override
        public boolean isWriteAwaitable() {
            return this.writeAwaiter != null;
        }

        @Override
        public StageFuture<Message> respond() {
            return this.respondAwaiter;
        }

        @Override
        public StageFuture<Void> written() {
            return this.writeAwaiter;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("protocol", this.protocol)
                    .add("mode", this.mode)
                    .add("toMessage", this.toMessage)
                    .add("code", this.code)
                    .add("body", this.body)
                    .add("respondFuture", this.respondAwaiter != null)
                    .add("writeFuture", this.writeAwaiter != null)
                    .toString();
        }

    }

}
