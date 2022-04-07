package com.tny.game.basics.item;

import com.tny.game.common.enums.*;

/**
 * 事物类型接口
 *
 * @author KGTny
 */
public interface ItemType extends IntEnumerable {

	int ID_TAIL_SIZE = 1000000;

	/**
	 * 获取别名头
	 */
	String getAliasHead();

	String getDesc();

	default int getIdHead() {
		return getId() / ID_TAIL_SIZE;
	}

	default long createItemId(int index) {
		return Long.parseLong(this.getIdHead() + "" + index);
	}

	default String alisaOf(String alisa) {
		return getIdHead() + "$" + alisa;
	}

}