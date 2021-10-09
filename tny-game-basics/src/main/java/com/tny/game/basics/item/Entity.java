package com.tny.game.basics.item;

import com.tny.game.common.tag.*;

import java.util.Set;

public interface Entity<M extends Model> extends AnyOwned, Taggable {

	/**
	 * @return 玩家ID
	 */
	@Override
	long getPlayerId();

	/**
	 * @return 获取对象别名
	 */
	String getAlias();

	/**
	 * @return 获取该事物对象ID
	 */
	int getItemId();

	/**
	 * @return 获取该事物对象的模型
	 */
	M getModel();

	@Override
	default Set<Object> tags() {
		return this.getModel().tags();
	}

}
