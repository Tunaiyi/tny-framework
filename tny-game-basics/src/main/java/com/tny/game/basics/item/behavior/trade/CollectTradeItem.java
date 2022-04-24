package com.tny.game.basics.item.behavior.trade;

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.*;

import java.util.*;

import static com.tny.game.common.number.NumberAide.*;

public class CollectTradeItem implements TradeItem<StuffModel> {

    private long id;

    private AlterType alertType;

    private StuffModel itemModel;

    private Number number;

    private boolean valid;

    private Map<DemandParam, Object> paramMap = new HashMap<>();

    public CollectTradeItem(TradeItem<?> item) {
        super();
        this.id = item.getId();
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
        if (this.getItemModel().getId() == item.getItemModel().getId()) {
            if (item.isValid()) {
                this.number = add(this.number, item.getNumber());
                this.valid = true;
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <SI extends StuffModel> SI getItemModel() {
        return (SI)this.itemModel;
    }

    @Override
    public long getId() {
        return 0;
    }

    @Override
    public Number getNumber() {
        return this.number;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <P> P getParam(DemandParam param) {
        Object value = this.paramMap.get(param);
        if (value == null) {
            return null;
        }
        return (P)value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <P> P getParam(DemandParam param, P defaultValue) {
        Object value = this.paramMap.get(param);
        return value == null ? defaultValue : (P)value;
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
