package com.tny.game.basics.item.model;

import com.tny.game.basics.item.*;

/**
 * 抽象xml映射事物模型
 *
 * @author KGTny
 */
public abstract class MemoryItemModel extends BaseItemModel {

	private final ItemType itemType;

	protected MemoryItemModel(ItemType itemType, String alisa, String desc) {
		this.id = itemType.getId();
		this.itemType = itemType;
		this.alias = itemType.alisaOf(alisa);
		this.desc = desc;
	}

	@Override
	public ItemType itemType() {
		return itemType;
	}

}
