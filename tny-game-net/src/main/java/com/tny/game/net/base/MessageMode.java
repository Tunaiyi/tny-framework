package com.tny.game.net.base;

import com.tny.game.LogUtils;

public enum MessageMode {

    /**
     * 处理推送
     */
    PUSH(Integer.MIN_VALUE, -1),

    /**
     * 处理响应
     */
    REQUEST(0, 0),

    /**
     * 处理响应
     */
    RESPONSE(0, Integer.MAX_VALUE);

    private int min;
    private int max;

    MessageMode(int min, int max) {
        this.min = min;
        this.max = max;
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
        return min <= message.getToMessage() && message.getToMessage() <= max;
    }

}
