package com.tny.game.base.item.behavior.trade;

import com.tny.game.base.item.AlterType;
import com.tny.game.base.item.ItemModel;
import com.tny.game.base.item.TradeItem;
import com.tny.game.base.item.behavior.DemandParam;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CollectionTradeItem implements TradeItem<ItemModel> {

    private AlterType alertType;

    private ItemModel itemModel;

    private long number;

    private Map<DemandParam, Object> paramMap = new HashMap<DemandParam, Object>();

    public CollectionTradeItem(TradeItem<?> item) {
        super();
        this.itemModel = item.getItemModel();
        this.number = item.getNumber();
        this.alertType = item.getAlertType();
        if (this.paramMap != null) {
            this.paramMap.putAll(item.getParamMap());
        }
    }

    @Override
    public AlterType getAlertType() {
        return this.alertType;
    }

    protected void collect(TradeItem<?> item) {
        if (this.getItemModel().getID() == item.getItemModel().getID()) {
            this.number += item.getNumber();
        }
    }

    @Override
    public ItemModel getItemModel() {
        return this.itemModel;
    }

    @Override
    public long getNumber() {
        return this.number;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <P> P getParam(DemandParam param) {
        Object value = this.paramMap.get(param);
        if (value == null)
            return null;
        return (P) value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <P> P getParam(DemandParam param, P defaultValue) {
        Object value = this.paramMap.get(param);
        return value == null ? defaultValue : (P) value;
    }

    @Override
    public Map<DemandParam, Object> getParamMap() {
        return Collections.unmodifiableMap(this.paramMap);
    }

    @Override
    public String toString() {
        return "SimpleTradeItem [alertType=" + this.alertType + ", itemModel=" + this.itemModel + ", number=" + this.number + "]";
    }
}
