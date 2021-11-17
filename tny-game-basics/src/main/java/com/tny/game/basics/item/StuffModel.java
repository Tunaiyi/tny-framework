package com.tny.game.basics.item;

/**
 * stuff模型接口，于数量相关的事物模型接口
 *
 * @author KGTny
 */
public interface StuffModel<N extends Number> extends ItemModel {

	/**
	 * @return 是否有数量限制 <br>
	 */
	boolean isNumberLimit();

	/**
	 * 数量上线 <br>
	 *
	 * @param stuff 计算参数
	 * @return -1 表示无限制
	 */
	N countNumberLimit(Stuff<?> stuff);

}