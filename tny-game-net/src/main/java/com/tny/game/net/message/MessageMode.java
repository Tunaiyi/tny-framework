package com.tny.game.net.message;

import com.tny.game.common.utils.Logs;

import java.util.function.Predicate;

public enum MessageMode {

    /**
     * 处理推送
     */
    PUSH(MessageAide::isPush),

    /**
     * 处理请求
     */
    REQUEST(MessageAide::isRequest),

    /**
     * 处理响应
     */
    RESPONSE(MessageAide::isResponse),

    /**
     * PING
     */
    PING() {
        @Override
        public boolean isMode(MessageHeader header) {
            return header.getMode() == this;
        }
    },


    /**
     * PONG
     */
    PONG() {
        @Override
        public boolean isMode(MessageHeader message) {
            return message.getMode() == this;
        }
    },

    //
    ;


    private Predicate<MessageHeader> checkMode;

    MessageMode() {
    }

    MessageMode(Predicate<MessageHeader> checkMode) {
        this.checkMode = checkMode;
    }

    public static MessageMode getMode(MessageHeader header) {
        if (REQUEST.isMode(header)) {
            return REQUEST;
        } else if (RESPONSE.isMode(header)) {
            return RESPONSE;
        } else if (PUSH.isMode(header)) {
            return PUSH;
        }
        throw new NullPointerException(Logs.format("mode [{}] is null", header.getToMessage()));
    }

    public static MessageMode getMode(int toMessage) {
        if (MessageAide.isRequest(toMessage)) {
            return REQUEST;
        } else if (MessageAide.isResponse(toMessage)) {
            return RESPONSE;
        } else if (MessageAide.isPush(toMessage)) {
            return PUSH;
        }
        throw new NullPointerException(Logs.format("mode [{}] is null", toMessage));
    }

    public boolean isMode(MessageHeader header) {
        return checkMode.test(header);
    }

    public boolean isMode(Message message) {
        return checkMode.test(message.getHeader());
    }

}
