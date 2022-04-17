package com.tny.game.basics.item;

import com.tny.game.basics.item.behavior.*;

import java.util.Map;

/**
 * 交易到的Item信息
 *
 * @param <I> ItemModel类型
 */
public interface DealItem<I extends StuffModel> {

	long getId();

	<SI extends I> SI getItemModel();

	default ItemType getItemType() {
		return getItemModel().getItemType();
	}

	default int getModelId() {
		return getItemModel().getId();
	}

	Number getNumber();

	Map<DemandParam, Object> getParamMap();

	<P> P getParam(DemandParam param, P defaultValue);

	<P> P getParam(DemandParam param);

}
