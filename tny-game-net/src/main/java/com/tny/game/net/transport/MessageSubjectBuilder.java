package com.tny.game.net.transport;

import com.google.common.base.MoreObjects;
import com.tny.game.common.result.ResultCode;
import com.tny.game.common.utils.Throws;
import com.tny.game.net.message.*;

import java.util.*;

import static com.tny.game.net.message.MessageAide.*;

/**
 * 消息上下文
 * Created by Kun Yang on 2017/2/16.
 */
public class MessageSubjectBuilder {

    private DefaultMessageSubject subject = new DefaultMessageSubject();

    /**
     * 创建推送消息主体 Builder
     *
     * @param protocol 协议号
     * @param code     消息结果码
     * @return
     */
    public static MessageSubjectBuilder pushBuilder(Protocol protocol, ResultCode code) {
        MessageSubjectBuilder builder = new MessageSubjectBuilder();
        builder.subject().init(protocol, code, PUSH_TO_MESSAGE_MAX_ID);
        return builder;
    }

    /**
     * 创建推送消息主体 Builder
     *
     * @param protocol 协议号
     * @return
     */
    public static MessageSubjectBuilder pushBuilder(Protocol protocol) {
        return pushBuilder(protocol, ResultCode.SUCCESS);
    }

    /**
     * 创建请求消息主体 Builder
     *
     * @param protocol 协议号
     * @return
     */
    public static MessageSubjectBuilder requestBuilder(Protocol protocol) {
        MessageSubjectBuilder builder = new MessageSubjectBuilder();
        builder.subject().init(protocol, ResultCode.SUCCESS, REQUEST_TO_MESSAGE_ID);
        return builder;
    }

    /**
     * 创建响应消息主体 Builder
     *
     * @param protocol  协议号
     * @param code      消息结果码
     * @param toMessage 响应的请求消息Id
     * @return
     */
    public static MessageSubjectBuilder respondBuilder(Protocol protocol, ResultCode code, long toMessage) {
        MessageSubjectBuilder builder = new MessageSubjectBuilder();
        builder.subject().init(protocol, code, toMessage);
        return builder;
    }

    /**
     * 创建响应消息主体 Builder
     *
     * @param protocol  协议号
     * @param toMessage 响应的请求消息Id
     * @return
     */
    public static MessageSubjectBuilder respondBuilder(Protocol protocol, long toMessage) {
        return respondBuilder(protocol, ResultCode.SUCCESS, toMessage);
    }

    /**
     * 创建响应消息主体 Builder
     *
     * @param code      消息结果码
     * @param respondTo 响应的请求消息
     * @return
     */
    public static MessageSubjectBuilder respondBuilder(ResultCode code, MessageHeader respondTo) {
        MessageSubjectBuilder builder = new MessageSubjectBuilder();
        builder.subject().init(respondTo, code, respondTo.getId());
        return builder;
    }

    /**
     * 创建响应消息主体 Builder
     *
     * @param code      消息结果码
     * @param respondTo 响应的请求消息
     * @return
     */
    public static MessageSubjectBuilder respondBuilder(ResultCode code, Message<?> respondTo) {
        return respondBuilder(code, respondTo.getHeader());
    }

    private MessageSubjectBuilder() {
    }

    public MessageSubjectBuilder setBody(Object body) {
        this.subject().setBody(body);
        return this;
    }

    public MessageSubjectBuilder setBodies(Object... bodies) {
        return setBodies(Arrays.asList(bodies));
    }

    public MessageSubjectBuilder setBodies(Collection<?> bodies) {
        this.subject().setBody(bodies);
        return this;
    }

    public MessageSubject build() {
        MessageSubject subject = this.subject();
        this.subject = null;
        return subject;
    }

    private DefaultMessageSubject subject() {
        if (subject == null)
            throw new IllegalArgumentException("builder has built");
        return this.subject;
    }

    private static class DefaultMessageSubject implements MessageSubject {

        private ResultCode code = ResultCode.SUCCESS;

        private int protocol;

        private long toMessage;

        private MessageMode mode;

        private Object body;

        private DefaultMessageSubject() {
        }

        private DefaultMessageSubject init(int protocol, ResultCode code, Long toMessage) {
            Throws.checkNotNull(code, "code is null");
            this.protocol = protocol;
            this.code = code;
            this.toMessage = toMessage;
            this.mode = MessageMode.getMode(toMessage);
            return this;
        }

        private DefaultMessageSubject init(Protocol protocol, ResultCode code, Long toMessage) {
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
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("protocol", protocol)
                    .add("mode", mode)
                    .add("toMessage", toMessage)
                    .add("code", code)
                    .add("body", body)
                    .toString();
        }

        private void setBody(Object body) {
            if (this.body != null)
                throw new IllegalArgumentException("body is exist");
            this.body = body;
        }

    }
}
