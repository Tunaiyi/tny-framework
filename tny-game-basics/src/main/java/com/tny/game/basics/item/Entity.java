package com.tny.game.basics.item;

import com.tny.game.common.tag.*;

import java.util.Set;

public interface Entity<M extends Model> extends Any, Taggable {

	/**
	 * @return 获取对象别名
	 */
	String getAlias();

	/**
	 * @return 获取该事物模型ID
	 */
	int getModelId();

	/**
	 * @return 获取该事物对象的模型
	 */
	M getModel();

	@Override
	default Set<Object> tags() {
		return this.getModel().tags();
	}

}
