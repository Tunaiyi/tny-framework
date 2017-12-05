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
        public boolean isMode(Message<?> message) {
            return message.getMode() == this;
        }
    },


    /**
     * PONG
     */
    PONG() {
        @Override
        public boolean isMode(Message<?> message) {
            return message.getMode() == this;
        }
    },

    //
    ;


    private Predicate<Message<?>> checkMode;

    MessageMode() {
    }

    MessageMode(Predicate<Message<?>> checkMode) {
        this.checkMode = checkMode;
    }

    public static MessageMode getMode(Message<?> message) {
        if (REQUEST.isMode(message)) {
            return REQUEST;
        } else if (RESPONSE.isMode(message)) {
            return RESPONSE;
        } else if (PUSH.isMode(message)) {
            return PUSH;
        }
        throw new NullPointerException(Logs.format("mode [{}] is null", message.getToMessage()));
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

    public boolean isMode(Message<?> message) {
        return checkMode.test(message);
    }

}
