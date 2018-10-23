package com.tny.game.net.transport;

import com.tny.game.net.message.Message;

/**
 * 终端消息处理过滤器
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-11 17:22
 */
public interface MessageHandleFilter<UID> {

    /**
     * 检测是否可以处理
     *
     * @return true 处理 false 不可处理
     */
    boolean isCanHandler(Message<UID> message);

}
