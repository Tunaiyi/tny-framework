package com.tny.game.actor.local;


import com.tny.game.actor.*;

/**
 * Created by Kun Yang on 16/4/26.
 */
public interface ActorDispatcher {

    /**
     * sender发送消息给当前Actor,
     *
     * @param message 发送消息
     * @param sender  发送者
     */
    default void tell(Object message, Actor sender) {
        sendMessage(message, sender, false);
    }

    /**
     * sender发送消息给当前Actor,
     *
     * @param message 发送消息
     * @param sender  发送者
     * @return 返回一个等待结果的Answer
     */
    default <V> Answer<V> ask(Object message, Actor sender) {
        return sendMessage(message, sender, true);
    }

    /**
     * 发送消息
     *
     * @param message    消息
     * @param sender     发送者
     * @param needAnswer 是否需要获取答案
     * @return 如果需要答案返回答案, 若不需要返回null
     */
    <V> Answer<V> sendMessage(Object message, Actor sender, boolean needAnswer);

    <ACT extends Actor> ACT getActor();

}
