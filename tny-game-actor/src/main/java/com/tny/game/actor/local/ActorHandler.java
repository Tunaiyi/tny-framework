package com.tny.game.actor.local;

/**
 * Deliver对象,负责处理消息.
 *
 * @author KGTny
 */
public interface ActorHandler<M> {

    Object handler(ActorMail<M> mail);

}