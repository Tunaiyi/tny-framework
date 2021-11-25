package com.tny.game.net.relay.link;

import com.tny.game.common.enums.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/3/2 3:36 下午
 */
public enum RelayLinkStatus implements Enumerable<Integer> {

	/**
	 * 初始化
	 **/
	INIT(1, false),
	/**
	 * 打开
	 **/
	OPEN(2, false),
	/**
	 * 打开
	 **/
	DISCONNECT(3, false),
	/**
	 * 关闭中
	 */
	CLOSING(4, true),
	/**
	 * 关闭
	 **/
	CLOSED(5, true);
	//
	;

	private final int id;

	private final boolean closeStatus;

	RelayLinkStatus(int id, boolean closeStatus) {
		this.id = id;
		this.closeStatus = closeStatus;
	}

	@Override
	public Integer getId() {
		return this.id;
	}

	public boolean isCloseStatus() {
		return this.closeStatus;
	}
}
