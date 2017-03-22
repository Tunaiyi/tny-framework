package com.tny.game.net.message;

import com.tny.game.LogUtils;

import java.util.function.Predicate;

public enum MessageMode {

    /**
     * 处理推送
     */
    PUSH(MessageUtils::isPush),

    /**
     * 处理请求
     */
    REQUEST(MessageUtils::isRequest),

    /**
     * 处理响应
     */
    RESPONSE(MessageUtils::isResponse);

    private Predicate<Message<?>> checkMode;

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
        throw new NullPointerException(LogUtils.format("mode [{}] is null", message.getToMessage()));
    }

    public boolean isMode(Message<?> message) {
        return checkMode.test(message);
    }

}
