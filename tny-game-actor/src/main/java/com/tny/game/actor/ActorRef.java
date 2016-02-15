package com.tny.game.actor;

/**
 * Actor的引用,开发过程中,无法获取到实际的Actor对象,只能获取到对应的Actor引用,
 * 可以通过actor引用进行消息发送.
 *
 * @author KGTny
 */
public interface ActorRef {

    /**
     * 获取Actor的路径信息对象
     *
     * @return 路径信息对象
     */
    ActorPath getPath();

    /**
     * 向当前Actor发送消息, 发送者为noSender
     *
     * @param message 发送的消息
     */
    void tell(Object message);

    /**
     * sender发送消息给当前Actor,
     *
     * @param message 发送消息
     * @param sender  发送者
     */
    void tell(Object message, ActorRef sender);

    /**
     * sender发送消息给当前Actor,
     *
     * @param message 发送消息
     * @param sender  发送者
     * @return 返回一个等待结果的Answer
     */
    <V> Answer<V> ask(Object message, ActorRef sender, Answer<V> answer);

    boolean isTerminated();

}