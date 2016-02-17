package com.tny.game.base.item.behavior.simple;

import com.tny.game.base.item.DealedItem;
import com.tny.game.base.item.DemandParamEntry;
import com.tny.game.base.item.ItemModel;
import com.tny.game.base.item.behavior.DemandParam;
import com.tny.game.base.item.behavior.DemandResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SimpleDealedtem<I extends ItemModel> implements DealedItem<I> {

    private I itemModel;

    private long number;

    private Map<DemandParam, Object> paramMap = new HashMap<DemandParam, Object>();

    public SimpleDealedtem() {
        super();
    }

    @SuppressWarnings("unchecked")
    public SimpleDealedtem(DemandResult result, DemandParamEntry<?>... entries) {
        super();
        this.itemModel = (I) result.getItemModel();
        this.number = result.getExpectValue(Integer.class);
        for (DemandParamEntry<?> entry : entries) {
            this.paramMap.put(entry.getParam(), entry.getValue());
        }
    }

    public SimpleDealedtem(I itemModel, long number, DemandParamEntry<?>... entries) {
        super();
        this.itemModel = itemModel;
        this.number = number;
        for (DemandParamEntry<?> entry : entries) {
            this.paramMap.put(entry.getParam(), entry.getValue());
        }
    }

    public SimpleDealedtem(DealedItem<I> item, long number) {
        super();
        this.itemModel = item.getItemModel();
        this.number = number;
        if (paramMap != null) {
            this.paramMap.putAll(item.getParamMap());
        }
    }

    @Override
    public I getItemModel() {
        return itemModel;
    }

    @Override
    public long getNumber() {
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
        Object value = this.getParam(param, defaultValue.getClass());
        return value == null ? defaultValue : (P) value;
    }

    @Override
    public Map<DemandParam, Object> getParamMap() {
        return Collections.unmodifiableMap(paramMap);
    }

    @Override
    public String toString() {
        return "SimpleTradeItem [itemModel=" + itemModel + ", number=" + number + "]";
    }

}
