package com.tny.game.base.item.behavior.trade;

import com.tny.game.base.item.AlterType;
import com.tny.game.base.item.ItemModel;
import com.tny.game.base.item.TradeItem;
import com.tny.game.base.item.behavior.DemandParam;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.tny.game.number.NumberUtils.*;

public class CollectionTradeItem implements TradeItem<ItemModel> {

    private AlterType alertType;

    private ItemModel itemModel;

    private Number number;

    private boolean valid;

    private Map<DemandParam, Object> paramMap = new HashMap<DemandParam, Object>();

    public CollectionTradeItem(TradeItem<?> item) {
        super();
        this.itemModel = item.getItemModel();
        this.number = item.getNumber();
        this.alertType = item.getAlertType();
        this.valid = item.isValid();
        if (this.paramMap != null) {
            this.paramMap.putAll(item.getParamMap());
        }
    }

    @Override
    public AlterType getAlertType() {
        return this.alertType;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    public void collect(TradeItem<?> item) {
        if (this.getItemModel().getID() == item.getItemModel().getID()) {
            if (item.isValid()) {
                this.number = add(this.number, item.getNumber());
                this.valid = true;
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <SI extends ItemModel> SI getItemModel() {
        return (SI) this.itemModel;
    }

    @Override
    public Number getNumber() {
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
