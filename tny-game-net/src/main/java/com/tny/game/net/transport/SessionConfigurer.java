package com.tny.game.net.transport;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-09 10:54
 */
public interface SessionConfigurer {

    /**
     * @return 缓存发送 message 最大数量
     */
    int getMaxCacheMessageSize();

}
