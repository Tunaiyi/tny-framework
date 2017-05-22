package com.tny.game.suite.base.capacity;

import com.tny.game.base.item.Item;
import com.tny.game.base.item.ItemModel;
import com.tny.game.suite.base.capacity.event.CapacityEvents;

import java.util.Map;

/**
 * 可缓存能力提供器
 * Created by Kun Yang on 16/3/12.
 */
public class DefaultCapacitySupply implements InnerCapacitySupply {

    private long playerID;

    private ItemModel model;

    private Item<?> item;

    public DefaultCapacitySupply(Item<?> item) {
        this.playerID = item.getPlayerID();
        this.model = item.getModel();
        this.item = item;
    }

    public DefaultCapacitySupply(long playerID, ItemModel model) {
        this.playerID = playerID;
        this.model = item.getModel();
    }

    @Override
    public Number getValue(Capacity capacity, Number defaultNum) {
        if (item != null) {
            return item.getAbility(defaultNum, capacity);
        } else {
            return model.getAbility(playerID, defaultNum, capacity);
        }
    }

    @Override
    public Number getValue(Capacity capacity) {
        if (item != null) {
            return item.getAbility(capacity, Number.class);
        } else {
            return model.getAbility(playerID, capacity, Number.class);
        }
    }

    @Override
    public Map<Capacity, Number> getAllCapacityValue() {
        if (item != null) {
            return item.getAbilitiesByType(Capacity.class, Number.class);
        } else {
            return model.getAbilitiesByType(playerID, Capacity.class, Number.class);
        }
    }

    @Override
    public boolean isHasValue(Capacity capacity) {
        return model.hasAbility(capacity);
    }


    @Override
    public long getPowerValue() {
        return 0;
    }

    // @Override
    // public Set<Capacity> getSupplyCapacities() {
    //     return model.getAbilityTypes(Capacity.class);
    // }


    @Override
    public void refresh(CapacitySupplier supplier) {
        CapacityEvents.ON_CHANGE.notify(this, supplier);
    }

    @Override
    public void invalid(CapacitySupplier supplier) {
        CapacityEvents.ON_INVALID.notify(this, supplier);
    }

    @Override
    public void effect(CapacitySupplier supplier) {
        CapacityEvents.ON_EFFECT.notify(this, supplier);
    }

}
