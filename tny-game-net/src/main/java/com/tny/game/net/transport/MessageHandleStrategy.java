package com.tny.game.net.transport;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2019-03-24 03:11
 */
public enum MessageHandleStrategy {

    /**
     * 处理
     */
    HANDLE,

    /**
     * 忽略
     */
    IGNORE,

    /**
     * 拦截抛出异常
     */
    THROW,

}
