package com.tny.game.net.endpoint;

import com.google.common.base.MoreObjects;
import com.tny.game.common.result.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

import java.util.Arrays;

import static com.tny.game.net.message.MessageAide.*;

/**
 * 消息上下文
 * Created by Kun Yang on 2017/2/16.
 */
public class MessageContexts {

    public static <UID> MessageContext<UID> createContext() {
        return new DefaultMessageContext<>();
    }

    /**
     * 创建推送消息上下文
     *
     * @param protocol 协议号
     * @return 创建的消息上下文
     */
    public static <UID> MessageContext<UID> push(Protocol protocol) {
        return push(protocol, ResultCode.SUCCESS);
    }

    /**
     * 创建推送消息上下文
     *
     * @param protocol 协议号
     * @param code     消息结果码
     * @return 创建的消息上下文
     */
    public static <UID> MessageContext<UID> push(Protocol protocol, ResultCode code) {
        DefaultMessageContext<UID> context = new DefaultMessageContext<>();
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
    public static <UID> MessageContext<UID> push(Protocol protocol, Object body) {
        DefaultMessageContext<UID> context = new DefaultMessageContext<>();
        context.init(MessageMode.PUSH, protocol, ResultCode.SUCCESS)
               .setBody(body);
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
    public static <UID> MessageContext<UID> push(Protocol protocol, ResultCode code, Object body) {
        DefaultMessageContext<UID> context = new DefaultMessageContext<>();
        context.init(MessageMode.PUSH, protocol, code)
               .setBody(body);
        return context;
    }

    /**
     * 创建请求消息上下文
     *
     * @param protocol 协议号
     * @return 创建的消息上下文
     */
    public static <UID> RequestContext<UID> request(Protocol protocol) {
        DefaultMessageContext<UID> context = new DefaultMessageContext<>();
        context.init(MessageMode.REQUEST, protocol, ResultCode.SUCCESS);
        return context;
    }

    /**
     * 创建请求消息上下文
     *
     * @param protocol 协议号
     * @param body     请求消息体
     * @return 创建的消息上下文
     */
    public static <UID> RequestContext<UID> request(Protocol protocol, Object body) {
        DefaultMessageContext<UID> context = new DefaultMessageContext<>();
        context.init(MessageMode.REQUEST, protocol, ResultCode.SUCCESS)
               .setBody(body);
        return context;
    }

    /**
     * 创建请求消息上下文
     *
     * @param protocol      协议号
     * @param requestParams 请求参数, Body 转为 List
     * @param <UID>
     * @return 创建的消息上下文
     */
    public static <UID> RequestContext<UID> requestParams(Protocol protocol, Object... requestParams) {
        DefaultMessageContext<UID> context = new DefaultMessageContext<>();
        context.init(MessageMode.REQUEST, protocol, ResultCode.SUCCESS)
               .setBody(Arrays.asList(requestParams));
        return context;
    }

    /**
     * 创建响应消息上下文
     *
     * @param protocol  协议号
     * @param toMessage 响应的请求消息Id
     * @return 创建的消息上下文
     */
    public static <UID> MessageContext<UID> respond(Protocol protocol, long toMessage) {
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
    public static <UID> MessageContext<UID> respond(Protocol protocol, ResultCode code, long toMessage) {
        DefaultMessageContext<UID> context = new DefaultMessageContext<>();
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
    public static <UID> MessageContext<UID> respond(Protocol protocol, Object body, long toMessage) {
        DefaultMessageContext<UID> context = new DefaultMessageContext<>();
        context.init(MessageMode.RESPONSE, protocol, ResultCode.SUCCESS, toMessage)
               .setBody(body);
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
    public static <UID> MessageContext<UID> respond(Protocol protocol, ResultCode code, Object body, long toMessage) {
        DefaultMessageContext<UID> context = new DefaultMessageContext<>();
        context.init(MessageMode.RESPONSE, protocol, code, toMessage)
               .setBody(body);
        return context;
    }

    /**
     * 创建响应消息上下文
     *
     * @param code        消息结果码
     * @param respondHead 响应的请求消息
     * @return 创建的消息上下文
     */
    public static <UID> MessageContext<UID> respond(ResultCode code, MessageHead respondHead) {
        DefaultMessageContext<UID> context = new DefaultMessageContext<>();
        context.init(MessageMode.RESPONSE, respondHead, code, respondHead.getId());
        return context;
    }

    /**
     * 创建响应消息上下文
     *
     * @param code           消息结果码
     * @param respondMessage 响应的请求消息
     * @return 创建的消息上下文
     */
    public static <UID> MessageContext<UID> respond(ResultCode code, Message<?> respondMessage) {
        return respond(code, respondMessage.getHead());
    }

    private static class DefaultMessageContext<UID> extends RequestContext<UID> {

        private ResultCode code = ResultCode.SUCCESS;

        private int protocol;

        private long toMessage;

        private MessageMode mode;

        private Object body;

        private Object tail;

        private Long writeTimeout;

        private Long respondTimeout;

        /**
         * 收到响应消息 Future, 只有 mode 为  request 才可以是使用
         */
        private volatile RespondFuture<UID> respondFuture;

        /**
         * 收到响应消息 Future, 只有 mode 为  request 才可以是使用
         */
        private volatile WriteMessagePromise writePromise;


        private DefaultMessageContext() {
        }

        private DefaultMessageContext<UID> init(MessageMode mode, Protocol protocol, ResultCode code) {
            return this.init(mode, protocol, code, EMPTY_MESSAGE_ID);
        }

        private DefaultMessageContext<UID> init(MessageMode mode, Protocol protocol, ResultCode code, Long toMessage) {
            ThrowAide.checkNotNull(protocol, "protocol is null");
            ThrowAide.checkNotNull(code, "code is null");
            this.protocol = protocol.getProtocolNumber();
            this.code = code;
            this.toMessage = toMessage;
            this.mode = mode;
            return this;
        }

        @Override
        public int getProtocolNumber() {
            return this.protocol;
        }

        @Override
        public int getCode() {
            return this.code.getCode();
        }

        @Override
        public ResultCode getResultCode() {
            return this.code;
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
        public <T> T getBody(Class<T> clazz) {
            return ObjectAide.as(this.body, clazz);
        }

        @Override
        public <T> T getBody(ReferenceType<T> clazz) {
            return ObjectAide.converTo(this.body, clazz);
        }

        @Override
        public boolean existTail() {
            return this.tail != null;
        }

        @Override
        public <T> T getTail(Class<T> clazz) {
            return ObjectAide.as(this.tail, clazz);
        }

        @Override
        public <T> T getTail(ReferenceType<T> clazz) {
            return ObjectAide.converTo(this.tail, clazz);
        }

        @Override
        public RequestContext<UID> setBody(Object body) {
            if (this.body != null)
                throw new IllegalArgumentException("body is exist");
            this.body = body;
            return this;
        }

        @Override
        public RequestContext<UID> setTail(Object tail) {
            this.tail = tail;
            return this;
        }


        @Override
        public long getWriteTimeout() {
            return this.writeTimeout == null ? -1 : this.writeTimeout;
        }

        @Override
        protected void setWriteMessagePromise(WriteMessagePromise writeFuture) {
            if (this.writePromise == null) {
                this.writePromise = writeFuture;
            }
        }

        @Override
        protected void setRespondFuture(RespondFuture<UID> respondFuture) {
            if (this.mode == MessageMode.REQUEST && this.respondFuture == null) {
                this.respondFuture = respondFuture;
            }
        }

        @Override
        protected WriteMessagePromise getWriteMessagePromise() {
            return this.writePromise;
        }

        @Override
        public void cancel(boolean mayInterruptIfRunning) {
            if (this.writePromise != null && !this.writePromise.isDone())
                this.writePromise.cancel(true);
            if (this.respondFuture != null && !this.respondFuture.isDone())
                this.respondFuture.cancel(true);
        }

        @Override
        public void fail(Throwable throwable) {
            if (this.writePromise != null && !this.writePromise.isDone())
                this.writePromise.failed(throwable);
            if (this.respondFuture != null && !this.respondFuture.isDone())
                this.respondFuture.completeExceptionally(throwable);
        }

        @Override
        public RequestContext<UID> willResponseFuture(long timeoutMills) {
            if (this.mode == MessageMode.REQUEST)
                this.respondTimeout = timeoutMills;
            return this;
        }

        @Override
        public RequestContext<UID> willWriteFuture(long timeoutMills) {
            this.writeTimeout = timeoutMills;
            return this;
        }

        @Override
        public boolean isNeedWriteFuture() {
            return this.writeTimeout != null && this.writePromise == null;
        }

        @Override
        public boolean isNeedResponseFuture() {
            return this.respondTimeout != null && this.respondFuture == null;
        }

        // @Override
        // public RequestContext<UID> willSendFuture(Consumer<MessageSendFuture<UID>> consumer) {
        //     consumer.accept(this.loadOrCreateSendFuture());
        //     return this;
        // }
        //
        // @Override
        // public RequestContext<UID> willSendFuture() {
        //     this.loadOrCreateSendFuture();
        //     return this;
        // }
        //
        // @Override
        // public MessageContext<UID> willWriteCallback(WriteMessageListener<UID> callback) {
        //     this.loadOrCreateSendFuture().whenComplete(
        //             (msg, cause) -> callback.onWrite(cause == null, cause, msg, this));
        //     return this;
        // }
        //
        // @Override
        // public RequestContext<UID> willResponseFuture(Consumer<RespondFuture<UID>> consumer, long lifeTime) {
        //     consumer.accept(this.loadOrCreateRespondFuture(lifeTime));
        //     return this;
        // }
        //
        // @Override
        // public RequestContext<UID> willResponseFuture(long lifeTime) {
        //     this.loadOrCreateRespondFuture(lifeTime);
        //     return this;
        // }
        //
        // private RespondFuture<UID> loadOrCreateRespondFuture(long lifeTime) {
        //     if (this.respondFuture != null)
        //         return this.respondFuture;
        //     synchronized (this) {
        //         if (this.respondFuture == null)
        //             this.respondFuture = new RespondFuture<>(lifeTime);
        //     }
        //     return this.respondFuture;
        // }
        //
        // private MessageSendFuture<UID> loadOrCreateSendFuture() {
        //     if (this.sendFuture != null)
        //         return this.sendFuture;
        //     synchronized (this) {
        //         if (this.sendFuture == null)
        //             this.sendFuture = new MessageSendFuture<>();
        //     }
        //     return this.sendFuture;
        // }

        @Override
        public boolean isHasRespondFuture() {
            return this.respondFuture != null;
        }

        @Override
        public boolean isHasWriteFuture() {
            return this.writePromise != null;
        }

        @Override
        public RespondFuture<UID> getRespondFuture() {
            return this.respondFuture;
        }

        @Override
        public WriteMessagePromise getWriteFuture() {
            return this.writePromise;
        }


        // @Override
        // public Object getBody() {
        //     return body;
        // }
        //
        // @Override
        // public Object getTail() {
        //     return tail;
        // }

        // @Override
        // public boolean isHasAttachment() {
        //     return this.attachment != null;
        // }
        //
        // @Override
        // public boolean isHasFuture() {
        //     return this.sendFuture != null || this.respondFuture != null;
        // }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                              .add("protocol", this.protocol)
                              .add("mode", this.mode)
                              .add("toMessage", this.toMessage)
                              .add("code", this.code)
                              .add("body", this.body)
                              .add("tail", this.tail)
                              // .add("sendFuture", sendFuture != null)
                              .add("respondFuture", this.respondFuture != null)
                              .add("writeFuture", this.writePromise != null)
                              .toString();
        }

    }
}
