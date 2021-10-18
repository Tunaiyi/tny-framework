package com.tny.game.basics.item;

public interface Any {

	/**
	 * @return 获取对象ID (非全局唯一, 确保相同 playerId 内唯一)
	 */
	long getId();

	/**
	 * @return 所属ID
	 */
	long getPlayerId();

}
