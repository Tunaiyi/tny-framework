package com.tny.game.net.transport;

import com.tny.game.net.message.*;

import java.util.Collection;

/**
 * 消息收集器
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/21 4:05 下午
 */
public interface MessagesCollector {

    Collection<Message> collect();

}
