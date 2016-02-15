package com.tny.game.actor.system;

import com.tny.game.actor.ActorRef;

/**
 * Actor终止的系统事件
 * @author KGTny
 *
 */
public class DeathWatchedSysMsg extends BaseSystemMessage {

	/**
	 * 死亡Actor
	 */
	private ActorRef actor;

	/**
	 * 是否是被监控的Actor直接发送的?
	 */
	private boolean existenceConfirmed;

	/**
	 * 是否是远程Actor对象关闭
	 */
	private boolean addressTerminated;

	public static DeathWatchedSysMsg message(ActorRef actor, boolean existenceConfirmed, boolean addressTerminated) {
		return new DeathWatchedSysMsg(actor, existenceConfirmed, addressTerminated);
	}

	private DeathWatchedSysMsg(ActorRef actor, boolean existenceConfirmed, boolean addressTerminated) {
		super(SystemMessageType.DEATH_WATCHED);
		this.actor = actor;
		this.existenceConfirmed = existenceConfirmed;
		this.addressTerminated = addressTerminated;
	}

	/**
	 * 死亡的Actor
	 * @return
	 */
	public ActorRef getActor() {
		return actor;
	}

	/**
	 * @return
	 * 	如果为true : 表明是被监控的Actor直接发送的
	 *  如果为false : 表明不是被监控的Actor直接发送的,而是又其他对象发送的.
	 */
	public boolean isExistenceConfirmed() {
		return existenceConfirmed;
	}

	/**
	 * 是否是远程Actor终止
	 * @return
	 */
	public boolean isAddressTerminated() {
		return addressTerminated;
	}

}
