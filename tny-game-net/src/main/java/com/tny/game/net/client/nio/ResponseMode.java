package com.tny.game.net.client.nio;

public enum ResponseMode {

    /**
     * 都处理
     */
    ALL(Integer.MIN_VALUE, Integer.MAX_VALUE),

    /**
     * 处理推送
     */
    PUSH(Integer.MIN_VALUE, -1),

    /**
     * 处理请求
     */
    REPLY(0, Integer.MAX_VALUE);

    private int min;
    private int max;

    private ResponseMode(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public boolean isHandle(int id) {
        return min <= id && id <= max;
    }

}
