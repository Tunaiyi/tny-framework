package com.tny.game.net.base;

public interface SendResult {

    /**
     * 是否完成
     */
    boolean isDone();

    /**
     * 是否成功
     */
    boolean isSuccess();

    /**
     * 获取失败原因
     */
    Throwable getCause();

    long getUserID();

    String getUserGroup();
}
