package com.tny.game.net.dispatcher;

import com.tny.game.common.result.ResultCode;

/**
 * Created by Kun Yang on 2017/2/16.
 */
public class MessageContent<R> {

    private ResultCode code = ResultCode.SUCCESS;

    private Object body;

    private Integer toMessage;

    private MessageSentHandler sentHandler;

    private MessageFuture<R> messageFuture;

    public static <O> MessageContent<O> of(Object body) {
        return new MessageContent<>(ResultCode.SUCCESS, body, null);
    }

    public static <O> MessageContent<O> of(ResultCode code, Object body) {
        return new MessageContent<>(code, body, null);
    }

    public static <O> MessageContent<O> of(ResultCode code, Object body, Integer toMessage) {
        return new MessageContent<>(code, body, toMessage);
    }

    private MessageContent() {
    }

    private MessageContent(ResultCode code, Object body, Integer toMessage) {
        this.code = code;
        this.body = body;
        this.toMessage = toMessage;
        this.sentHandler = sentHandler;
        this.messageFuture = messageFuture;
    }

    public ResultCode getCode() {
        return code;
    }

    public Object getBody() {
        return body;
    }

    public Integer getToMessage() {
        return toMessage;
    }

    public MessageSentHandler getSentHandler() {
        return sentHandler;
    }

    public MessageFuture<R> getMessageFuture() {
        return messageFuture;
    }


    public MessageContent setSentHandler(MessageSentHandler sentHandler) {
        this.sentHandler = sentHandler;
        return this;
    }

    public MessageContent setMessageFuture(MessageFuture<R> messageFuture) {
        this.messageFuture = messageFuture;
        return this;
    }

    public MessageContent setMessageAction(MessageAction<R> action) {
        this.messageFuture = new MessageFuture<>(action);
        return this;
    }

    public MessageContent setMessageAction(MessageAction<R> action, long timeout) {
        this.messageFuture = new MessageFuture<>(action, timeout);
        return this;
    }

}