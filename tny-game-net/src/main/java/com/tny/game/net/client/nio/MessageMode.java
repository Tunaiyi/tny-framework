package com.tny.game.net.client.nio;

import com.tny.game.net.base.Message;

public enum MessageMode {

    /**
     * 都处理
     */
    ALL(Integer.MIN_VALUE, Integer.MAX_VALUE),

    /**
     * 处理推送
     */
    PUSH(-1, -1),

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

    public boolean isHandle(Message message) {
        return min <= message.getToMessage() && message.getToMessage() <= max;
    }

}
