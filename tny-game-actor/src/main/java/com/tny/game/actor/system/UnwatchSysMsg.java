package com.tny.game.actor.system;

import com.tny.game.actor.ActorRef;

/**
 * 请求结束监控子Actor终止状态系统事件
 * @author KGTny
 *
 */
public class UnwatchSysMsg extends BaseSystemMessage {

	/**
	 * 监控目标
	 */
	private ActorRef watchee;

	/**
	 * 监控者
	 */
	private ActorRef watcher;

	public static UnwatchSysMsg message(ActorRef watchee, ActorRef watcher) {
		return new UnwatchSysMsg(watchee, watcher);
	}

	private UnwatchSysMsg(ActorRef watchee, ActorRef watcher) {
		super(SystemMessageType.UNWATCH);
	}

	/**
	 * @return 返回获取监控目标
	 */
	public ActorRef getWatchee() {
		return watchee;
	}

	/**
	 * @return 返回获取监控者
	 */
	public ActorRef getWatcher() {
		return watcher;
	}

}
