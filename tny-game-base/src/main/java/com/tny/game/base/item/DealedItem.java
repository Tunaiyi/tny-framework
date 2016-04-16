package com.tny.game.base.item;

import com.tny.game.base.item.behavior.DemandParam;

import java.util.Map;

public interface DealedItem<I extends ItemModel> {

    <SI extends I> SI getItemModel();

    Number getNumber();

    Map<DemandParam, Object> getParamMap();

    <P> P getParam(DemandParam param, P defaultValue);

    <P> P getParam(DemandParam param);

}
