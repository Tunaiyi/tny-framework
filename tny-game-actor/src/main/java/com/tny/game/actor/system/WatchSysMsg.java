package com.tny.game.actor.system;

import com.tny.game.actor.InternalActorRef;

/**
 * 请求监控子Actor终止状态的系统事件
 * @author KGTny
 *
 */
public class WatchSysMsg extends BaseSystemMessage {

	/**
	 * 监控目标
	 */
	private InternalActorRef watchee;

	/**
	 * 监控者
	 */
	private InternalActorRef watcher;

	public static WatchSysMsg message(InternalActorRef watchee, InternalActorRef watcher) {
		return new WatchSysMsg(watchee, watcher);
	}

	private WatchSysMsg(InternalActorRef watchee, InternalActorRef watcher) {
		super(SystemMessageType.WATCH);
	}

	/**
	 * @return 返回获取监控目标
	 */
	public InternalActorRef getWatchee() {
		return watchee;
	}

	/**
	 * @return 返回获取监控者
	 */
	public InternalActorRef getWatcher() {
		return watcher;
	}

}
