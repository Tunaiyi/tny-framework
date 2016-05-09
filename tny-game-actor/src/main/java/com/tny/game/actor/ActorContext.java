package com.tny.game.actor;

/**
 * Actor上下文对象,负责管理Actor的生命周期,和Actor的消息监听
 *
 * @author KGTny
 */
public interface ActorContext<ID, ACT extends Actor<ID, ?>> extends ActorFactory<ID, ACT> {

}
