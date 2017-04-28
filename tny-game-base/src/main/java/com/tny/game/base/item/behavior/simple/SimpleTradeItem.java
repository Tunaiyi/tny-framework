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

    private Number number;

    private boolean valid;

    private Map<DemandParam, Object> paramMap = new HashMap<>();

    public SimpleTradeItem(DemandResult result, AlterType alertType, DemandParamEntry<?>... entries) {
        this(result, alertType, true, entries);
    }

    @SuppressWarnings("unchecked")
    public SimpleTradeItem(DemandResult result, AlterType alertType, boolean valid, DemandParamEntry<?>... entries) {
        this.alertType = alertType;
        this.itemModel = (I) result.getItemModel();
        this.number = result.getExpectValue(Integer.class);
        this.valid = valid;
        for (DemandParamEntry<?> entry : entries) {
            this.paramMap.put(entry.getParam(), entry.getValue());
        }
    }

    public SimpleTradeItem(I itemModel, Number number, AlterType alertType, Map<DemandParam, Object> paramMap) {
        this(itemModel, number, alertType, true, paramMap);
    }

    public SimpleTradeItem(I itemModel, Number number, AlterType alertType, boolean valid, Map<DemandParam, Object> paramMap) {
        super();
        this.itemModel = itemModel;
        this.number = number;
        this.alertType = alertType;
        this.valid = valid;
        if (paramMap != null) {
            this.paramMap.putAll(paramMap);
        }
    }

    public SimpleTradeItem(TradeItem<I> item, Number number) {
        this(item, number, true);
    }

    public SimpleTradeItem(TradeItem<I> item, Number number, boolean valid) {
        super();
        this.itemModel = item.getItemModel();
        this.number = number;
        this.alertType = item.getAlertType();
        this.valid = valid;
        if (paramMap != null) {
            this.paramMap.putAll(item.getParamMap());
        }
    }

    public SimpleTradeItem(I model, Number number, AlterType alertType) {
        this(model, number, alertType, true);
    }

    public SimpleTradeItem(I model, Number number, AlterType alertType, boolean valid) {
        super();
        this.itemModel = model;
        this.number = number;
        this.alertType = alertType;
        this.valid = valid;
    }

    public SimpleTradeItem(I model, Number number) {
        this(model, number, true);
    }

    public SimpleTradeItem(I model, Number number, boolean valid) {
        this(model, number, AlterType.CHECK, valid);
    }

    @Override
    public AlterType getAlertType() {
        return alertType;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <SI extends I> SI getItemModel() {
        return (SI) itemModel;
    }

    @Override
    public Number getNumber() {
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
