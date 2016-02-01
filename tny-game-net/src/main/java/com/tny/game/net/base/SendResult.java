package com.tny.game.net.base;

public interface SendResult {

    /**
     * 是否完成
     */
    public boolean isDone();

    /**
     * 是否成功
     */
    public boolean isSuccess();

    /**
     * 获取失败原因
     */
    public Throwable getCause();

    long getUserID();

    String getUserGroup();
}
