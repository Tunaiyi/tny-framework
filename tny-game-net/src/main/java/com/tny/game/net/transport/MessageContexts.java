package com.tny.game.net.transport;

import com.google.common.base.MoreObjects;
import com.tny.game.common.result.ResultCode;
import com.tny.game.common.utils.Throws;
import com.tny.game.net.message.*;

import java.util.Arrays;
import java.util.function.Consumer;

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
        context.init(protocol, code, PUSH_TO_MESSAGE_ID);
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
        context.init(protocol, ResultCode.SUCCESS, PUSH_TO_MESSAGE_ID)
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
        context.init(protocol, code, PUSH_TO_MESSAGE_ID)
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
        context.init(protocol, ResultCode.SUCCESS, REQUEST_TO_MESSAGE_ID);
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
        context.init(protocol, ResultCode.SUCCESS, REQUEST_TO_MESSAGE_ID)
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
        context.init(protocol, ResultCode.SUCCESS, REQUEST_TO_MESSAGE_ID)
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
        context.init(protocol, code, toMessage);
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
        context.init(protocol, ResultCode.SUCCESS, toMessage)
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
        context.init(protocol, code, toMessage)
                .setBody(body);
        return context;
    }

    /**
     * 创建响应消息上下文
     *
     * @param code          消息结果码
     * @param respondHeader 响应的请求消息
     * @return 创建的消息上下文
     */
    public static <UID> MessageContext<UID> respond(ResultCode code, MessageHeader respondHeader) {
        DefaultMessageContext<UID> context = new DefaultMessageContext<>();
        context.init(respondHeader, code, respondHeader.getId());
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
        return respond(code, respondMessage.getHeader());
    }

    private static class DefaultMessageContext<UID> implements RequestContext<UID> {

        private ResultCode code = ResultCode.SUCCESS;

        private int protocol;

        private long toMessage;

        private MessageMode mode;

        private Object body;

        private Object attachment;

        /**
         * 发送消息 Future
         */
        private volatile MessageSendFuture<UID> sendFuture;

        /**
         * 收到响应消息 Future, 只有 mode 为  request 才可以是使用
         */
        private volatile RespondFuture<UID> respondFuture;


        private DefaultMessageContext() {
        }

        private DefaultMessageContext<UID> init(int protocol, ResultCode code, Long toMessage) {
            Throws.checkNotNull(code, "code is null");
            this.protocol = protocol;
            this.code = code;
            this.toMessage = toMessage;
            this.mode = MessageMode.getMode(toMessage);
            return this;
        }

        private DefaultMessageContext<UID> init(Protocol protocol, ResultCode code, Long toMessage) {
            Throws.checkNotNull(protocol, "protocol is null");
            Throws.checkNotNull(code, "code is null");
            this.protocol = protocol.getProtocolNumber();
            this.code = code;
            this.toMessage = toMessage;
            this.mode = MessageMode.getMode(toMessage);
            return this;
        }

        @Override
        public int getProtocolNumber() {
            return protocol;
        }

        @Override
        public ResultCode getCode() {
            return code;
        }

        @Override
        public Object getBody() {
            return body;
        }

        @Override
        public MessageMode getMode() {
            return this.mode;
        }

        @Override
        public long getToMessage() {
            return toMessage;
        }

        @Override
        public MessageContext<UID> setBody(Object body) {
            if (this.body != null)
                throw new IllegalArgumentException("body is exist");
            this.body = body;
            return this;
        }

        @Override
        public RequestContext<UID> setAttachment(Object attachment) {
            this.attachment = attachment;
            return this;
        }

        @Override
        public RequestContext<UID> willSendFuture(Consumer<MessageSendFuture<UID>> consumer) {
            consumer.accept(this.loadOrCreateSendFuture());
            return this;
        }

        @Override
        public RequestContext<UID> willSendFuture() {
            this.loadOrCreateSendFuture();
            return this;
        }

        @Override
        public RequestContext<UID> willResponseFuture(Consumer<RespondFuture<UID>> consumer, long lifeTime) {
            consumer.accept(this.loadOrCreateRespondFuture(lifeTime));
            return this;
        }

        @Override
        public RequestContext<UID> willResponseFuture(long lifeTime) {
            this.loadOrCreateRespondFuture(lifeTime);
            return this;
        }

        private RespondFuture<UID> loadOrCreateRespondFuture(long lifeTime) {
            if (this.respondFuture != null)
                return this.respondFuture;
            synchronized (this) {
                if (this.respondFuture == null)
                    this.respondFuture = new RespondFuture<>(lifeTime);
            }
            return this.respondFuture;
        }

        private MessageSendFuture<UID> loadOrCreateSendFuture() {
            if (this.sendFuture != null)
                return this.sendFuture;
            synchronized (this) {
                if (this.sendFuture == null)
                    this.sendFuture = new MessageSendFuture<>();
            }
            return this.sendFuture;
        }

        @Override
        public boolean isHasSendFuture() {
            return this.sendFuture != null;
        }

        @Override
        public boolean isHasRespondFuture() {
            return this.respondFuture != null;
        }

        @Override
        public MessageSendFuture<UID> getSendFuture() {
            return sendFuture;
        }

        @Override
        public RespondFuture<UID> getRespondFuture() {
            return respondFuture;
        }

        @Override
        public Object getAttachment() {
            return attachment;
        }

        @Override
        public boolean isHasAttachment() {
            return this.attachment != null;
        }

        @Override
        public boolean isHasFuture() {
            return this.sendFuture != null || this.respondFuture != null;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("protocol", protocol)
                    .add("mode", mode)
                    .add("toMessage", toMessage)
                    .add("code", code)
                    .add("body", body)
                    .add("sendFuture", sendFuture != null)
                    .add("respondFuture", respondFuture != null)
                    .add("attachment", attachment != null)
                    .toString();
        }

    }
}
