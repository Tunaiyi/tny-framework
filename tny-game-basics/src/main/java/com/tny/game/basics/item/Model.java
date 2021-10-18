package com.tny.game.basics.item;

import com.tny.game.common.tag.*;

public interface Model extends Taggable {

	/**
	 * @return 事物ID
	 */
	int getId();

	/**
	 * @return 获取别名
	 */
	String getAlias();

	/**
	 * @return 获取描述
	 */
	String getDesc();

}
