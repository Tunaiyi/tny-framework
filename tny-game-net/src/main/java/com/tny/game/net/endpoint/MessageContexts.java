package com.tny.game.net.endpoint;

import com.google.common.base.MoreObjects;
import com.tny.game.common.result.*;
import com.tny.game.common.type.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

import static com.tny.game.net.message.MessageAide.*;
import static com.tny.game.net.transport.TransportConstants.*;

/**
 * 消息上下文
 * Created by Kun Yang on 2017/2/16.
 */
public class MessageContexts {

    public static <UID> MessageContext createContext() {
        return new DefaultMessageContext<>();
    }

    /**
     * 创建推送消息上下文
     *
     * @param protocol 协议号
     * @return 创建的消息上下文
     */
    public static <UID> MessageContext push(Protocol protocol) {
        return push(protocol, ResultCode.SUCCESS);
    }

    /**
     * 创建推送消息上下文
     *
     * @param protocol 协议号
     * @param code     消息结果码
     * @return 创建的消息上下文
     */
    public static <UID> MessageContext push(Protocol protocol, ResultCode code) {
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
    public static <UID> MessageContext push(Protocol protocol, Object body) {
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
    public static <UID> MessageContext push(Protocol protocol, ResultCode code, Object body) {
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
    public static <UID> RequestContext request(Protocol protocol) {
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
    public static <UID> RequestContext request(Protocol protocol, Object body) {
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
     * @param <UID>         ID
     * @return 创建的消息上下文
     */
    public static <UID> RequestContext requestParams(Protocol protocol, Object... requestParams) {
        DefaultMessageContext<UID> context = new DefaultMessageContext<>();
        context.init(MessageMode.REQUEST, protocol, ResultCode.SUCCESS)
                .setBody(new MessageParamList(requestParams));
        return context;
    }

    /**
     * 创建响应消息上下文
     *
     * @param protocol  协议号
     * @param toMessage 响应的请求消息Id
     * @return 创建的消息上下文
     */
    public static <UID> MessageContext respond(Protocol protocol, long toMessage) {
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
    public static <UID> MessageContext respond(Protocol protocol, ResultCode code, long toMessage) {
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
    public static <UID> MessageContext respond(Protocol protocol, Object body, long toMessage) {
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
    public static <UID> MessageContext respond(Protocol protocol, ResultCode code, Object body, long toMessage) {
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
    public static <UID> MessageContext respond(ResultCode code, MessageHead respondHead) {
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
    public static <UID> MessageContext respond(ResultCode code, Message respondMessage) {
        return respond(code, respondMessage.getHead());
    }

    private static class DefaultMessageContext<UID> extends RequestContext {

        private ResultCode code = ResultCode.SUCCESS;

        private int protocol;

        private int line;

        private long toMessage;

        private MessageMode mode;

        private Object body;

        private long writeTimeout = TIMEOUT_NONE;

        private long respondTimeout;

        /**
         * 收到响应消息 Future, 只有 mode 为  request 才可以是使用
         */
        private volatile RespondFuture respondFuture;

        /**
         * 收到响应消息 Future, 只有 mode 为  request 才可以是使用
         */
        private volatile WriteMessagePromise writePromise;

        private volatile List<WriteMessageListener> listeners;

        private DefaultMessageContext() {
        }

        private DefaultMessageContext<UID> init(MessageMode mode, Protocol protocol, ResultCode code) {
            return this.init(mode, protocol, code, EMPTY_MESSAGE_ID);
        }

        private DefaultMessageContext<UID> init(MessageMode mode, Protocol protocol, ResultCode code, Long toMessage) {
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
            return ObjectAide.convertTo(this.body, clazz);
        }

        @Override
        public <T> T getBody(ReferenceType<T> clazz) {
            return ObjectAide.convertTo(this.body, clazz);
        }

        @Override
        public RequestContext setBody(Object body) {
            if (this.body != null) {
                throw new IllegalArgumentException("body is exist");
            }
            this.body = body;
            return this;
        }

        @Override
        public long getWriteTimeout() {
            return this.writeTimeout == TIMEOUT_NONE ? TIMEOUT_NEVER : this.writeTimeout;
        }

        @Override
        protected void setWriteMessagePromise(WriteMessagePromise writeFuture) {
            if (this.writePromise == null) {
                this.writePromise = writeFuture;
                if (CollectionUtils.isNotEmpty(this.listeners)) {
                    this.writePromise.addWriteListeners(this.listeners);
                }
            }
        }

        @Override
        protected void setRespondFuture(RespondFuture respondFuture) {
            if (this.mode == MessageMode.REQUEST && this.respondFuture == null) {
                this.respondFuture = respondFuture;
            }
        }

        @Override
        public WriteMessageFuture getWriteMessageFuture() {
            return this.writePromise;
        }

        @Override
        public void cancel(boolean mayInterruptIfRunning) {
            if (this.writePromise != null && !this.writePromise.isDone()) {
                this.writePromise.cancel(true);
            }
            if (this.respondFuture != null && !this.respondFuture.isDone()) {
                this.respondFuture.cancel(true);
            }
        }

        @Override
        protected void fail(Throwable throwable) {
            if (this.writePromise != null && !this.writePromise.isDone()) {
                this.writePromise.failed(throwable);
            }
            if (this.respondFuture != null && !this.respondFuture.isDone()) {
                this.respondFuture.completeExceptionally(throwable);
            }
        }

        @Override
        public RequestContext willResponseFuture(long timeoutMills) {
            if (this.mode == MessageMode.REQUEST) {
                this.respondTimeout = timeoutMills;
            }
            return this;
        }

        @Override
        public RequestContext willWriteFuture(long timeoutMills) {
            this.writeTimeout = timeoutMills;
            return this;
        }

        @Override
        public MessageContext willWriteFuture(WriteMessageListener listener) {
            if (listener != null) {
                this.writeTimeout = TIMEOUT_NEVER;
                listeners().add(listener);
            }
            return this;
        }

        @Override
        public MessageContext willWriteFuture(Collection<WriteMessageListener> listeners) {
            if (CollectionUtils.isNotEmpty(listeners)) {
                this.writeTimeout = TIMEOUT_NEVER;
                listeners().addAll(listeners);
            }
            return this;
        }

        private List<WriteMessageListener> listeners() {
            if (this.listeners != null) {
                return this.listeners;
            }
            synchronized (this) {
                if (this.listeners != null) {
                    return this.listeners;
                }
                this.listeners = new LinkedList<>();
            }
            return this.listeners;
        }

        @Override
        public boolean isNeedWriteFuture() {
            return this.writeTimeout != TIMEOUT_NONE;
        }

        @Override
        public boolean isNeedResponseFuture() {
            return this.respondTimeout != TIMEOUT_NONE;
        }

        @Override
        public boolean isHasRespondFuture() {
            return this.respondFuture != null;
        }

        @Override
        public boolean isHasWriteFuture() {
            return this.writePromise != null;
        }

        @Override
        public RespondFuture getRespondFuture() {
            return this.respondFuture;
        }

        @Override
        public WriteMessagePromise getWriteFuture() {
            return this.writePromise;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("protocol", this.protocol)
                    .add("mode", this.mode)
                    .add("toMessage", this.toMessage)
                    .add("code", this.code)
                    .add("body", this.body)
                    // .add("sendFuture", sendFuture != null)
                    .add("respondFuture", this.respondFuture != null)
                    .add("writeFuture", this.writePromise != null)
                    .toString();
        }

    }

}
