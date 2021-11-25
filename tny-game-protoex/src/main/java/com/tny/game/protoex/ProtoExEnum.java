package com.tny.game.protoex;

import com.tny.game.common.enums.*;

/**
 * protoEx枚举接口
 *
 * @author KGTny
 */
public interface ProtoExEnum extends Enumerable<Integer> {

	/**
	 * 枚举ID
	 *
	 * @return
	 */
	@Override
	Integer getId();

	/**
	 * 枚举名字
	 *
	 * @return
	 */
	String name();

}