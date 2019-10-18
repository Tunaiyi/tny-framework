package com.tny.game.suite.base.capacity;

import com.tny.game.base.item.Item;
import com.tny.game.suite.base.capacity.event.CapacityEvents;

import java.util.Map;
import java.util.Set;

/**
 * 可缓存能力提供器
 * Created by Kun Yang on 16/3/12.
 */
public class DefaultCapacitySupply implements InnerCapacitySupply {

    private long playerID;

    private CapacityItemModel model;

    private Item<?> item;

    public DefaultCapacitySupply(Item<?> item, CapacityItemModel model) {
        this.playerID = item.getPlayerId();
        this.item = item;
        this.model = model;
    }

    public DefaultCapacitySupply(Item<? extends CapacityItemModel> item) {
        this.playerID = item.getPlayerId();
        this.model = item.getModel();
        this.item = item;
    }

    public DefaultCapacitySupply(long playerID, CapacityItemModel model) {
        this.playerID = playerID;
        this.model = model;
    }

    @Override
    public Number getValue(Capacity capacity, Number defaultNum) {
        if (item != null) {
            return model.getAbility(item, defaultNum, capacity);
        } else {
            return model.getAbility(playerID, defaultNum, capacity);
        }
    }

    @Override
    public Set<CapacityGroup> getAllCapacityGroups() {
        return model.getCapacityGroups();
    }

    @Override
    public Number getValue(Capacity capacity) {
        if (item != null) {
            return model.getAbility(item, capacity, Number.class);
        } else {
            return model.getAbility(playerID, capacity, Number.class);
        }
    }

    @Override
    public Map<Capacity, Number> getAllValues() {
        if (item != null) {
            return model.getAbilitiesByType(item, Capacity.class, Number.class);
        } else {
            return model.getAbilitiesByType(playerID, Capacity.class, Number.class);
        }
    }

    @Override
    public boolean isHasValue(Capacity capacity) {
        return model.hasAbility(capacity);
    }

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
