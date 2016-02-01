package com.tny.game.base.item.behavior.simple;

import com.tny.game.base.item.AlterType;
import com.tny.game.base.item.DemandParamEntry;
import com.tny.game.base.item.ItemModel;
import com.tny.game.base.item.TradeItem;
import com.tny.game.base.item.behavior.DemandParam;
import com.tny.game.base.item.behavior.DemandResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SimpleTradeItem<I extends ItemModel> implements TradeItem<I> {

    private AlterType alertType;

    private I itemModel;

    private int number;

    private Map<DemandParam, Object> paramMap = new HashMap<DemandParam, Object>();

    public SimpleTradeItem() {
        super();
    }

    @SuppressWarnings("unchecked")
    public SimpleTradeItem(AlterType alertType, DemandResult result, DemandParamEntry<?>... entries) {
        super();
        this.alertType = alertType;
        this.itemModel = (I) result.getItemModel();
        this.number = result.getExpectValue(Integer.class);
        for (DemandParamEntry<?> entry : entries) {
            this.paramMap.put(entry.getParam(), entry.getValue());
        }
    }

    public SimpleTradeItem(AlterType alertType, I itemModel, int number, Map<DemandParam, Object> paramMap) {
        super();
        this.alertType = alertType;
        this.itemModel = itemModel;
        this.number = number;
        if (paramMap != null) {
            this.paramMap.putAll(paramMap);
        }
    }

    public SimpleTradeItem(TradeItem<I> item, int number) {
        super();
        this.itemModel = item.getItemModel();
        this.number = number;
        this.alertType = item.getAlertType();
        if (paramMap != null) {
            this.paramMap.putAll(item.getParamMap());
        }
    }

    public SimpleTradeItem(I model, int number, AlterType alertType) {
        super();
        this.itemModel = model;
        this.number = number;
        this.alertType = alertType;
    }

    public SimpleTradeItem(I model, int number) {
        this(model, number, AlterType.CHECK);
    }

    public SimpleTradeItem(I itemModel, AlterType alertType, int number, Map<DemandParam, Object> paramMap) {
        super();
        this.itemModel = itemModel;
        this.number = number;
        this.alertType = alertType;
        if (paramMap != null) {
            this.paramMap.putAll(paramMap);
        }
    }

    @Override
    public AlterType getAlertType() {
        return alertType;
    }

    @Override
    public I getItemModel() {
        return itemModel;
    }

    @Override
    public int getNumber() {
        return number;
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
        Object value = this.getParam(param);
        return value == null ? defaultValue : (P) value;
    }

    @Override
    public Map<DemandParam, Object> getParamMap() {
        return Collections.unmodifiableMap(paramMap);
    }

    @Override
    public String toString() {
        return "SimpleTradeItem [alertType=" + alertType + ", itemModel=" + itemModel + ", number=" + number + "]";
    }

}
