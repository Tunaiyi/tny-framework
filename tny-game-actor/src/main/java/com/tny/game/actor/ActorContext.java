package com.tny.game.actor;

import java.time.Duration;
import java.util.function.Consumer;

/**
 * Actor上下文对象,负责管理Actor的生命周期,和Actor的消息监听
 * @author KGTny
 */
public interface ActorContext extends ActorRefFactory {

	/**
	 * 设置接受超时区间
	 * @param duration 超时时间区间
     */
	void setReceiveTimeout(Duration duration);

	/**
	 * 将指定的Actor注册到当前上下文中,并进行消息监听处理
	 * @param actor 注册的Actor
	 */
	ActorRef watch(ActorRef actor);

	/**
	 * 将指定的Actor从当前上下文中注销,停止消息监听处理
	 * @param actor 注销的Actor
	 */
	ActorRef unwatch(ActorRef actor);

//	/**
//	 * 设置新的行为给予当前的上下问,
//	 * 	当discardOld=false的时候,旧的Actor不受新行为影响,新的Actor会收到影响
//	 *  当discardOld=true的时候,所有新旧Actor都会受新行为影响,
//	 * @param behavior 新的消息处理行为
//	 * @param discardOld 旧的处理器行为
//	 */
//	void become(Consumer<?> behavior, boolean discardOld);
//
//	/**
//	 * 移除become设置的处理器行为
//	 */
//	void unbecome();

	/**
	 * 获取当前的正在处理消息的Actor引用
	 * @return 返回当前的Actor引用
	 */
	ActorRef self();

	/**
	 * 获取当前的正在处理的消息的发送者Actor引用
	 * @return 返回正在处理消息的发送者Actor引用
	 */
	ActorRef getSender();

	/**
	 * @return 获取ActorSystem
     */
	ActorSystem getSystem();

}
