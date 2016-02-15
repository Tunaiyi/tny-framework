package com.tny.game.actor;

import java.util.Optional;
import java.util.Queue;

/**
 * 系统消息队列
 * Created by Kun Yang on 16/1/19.
 */
public interface SystemMessageQueue {

    /**
     * 将消息加入系统消息队列
     *
     * @param receiver 接收者
     * @param message  系统消息
     */
    void systemEnqueue(ActorRef receiver, SystemMessage message);

    /**
     * 是否有系统信息
     *
     * @return
     */
    boolean hasSystemMessages();

    /**
     * @return 清空并返回Queue中的消息
     */
    Optional<Queue<SystemMessage>> systemDrain();

    /**
     * 销毁消息队列
     */
    void dispose();

}
