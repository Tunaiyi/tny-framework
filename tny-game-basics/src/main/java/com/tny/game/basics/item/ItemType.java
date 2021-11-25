package com.tny.game.basics.item;

import com.tny.game.common.enums.*;

/**
 * 事物类型接口
 *
 * @author KGTny
 */
public interface ItemType extends Enumerable<Integer> {

	int ID_TAIL_SIZE = 1000000;

	/**
	 * 获取别名头
	 */
	String getAliasHead();

	String getDesc();

	default int getIdHead() {
		return getId() / ID_TAIL_SIZE;
	}

}