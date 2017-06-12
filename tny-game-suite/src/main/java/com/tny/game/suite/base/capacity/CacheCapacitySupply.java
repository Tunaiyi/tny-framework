package com.tny.game.suite.base.capacity;

import com.tny.game.base.item.Item;
import com.tny.game.base.item.ItemModel;
import com.tny.game.suite.base.capacity.event.CapacityEvents;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * 可缓存能力提供器
 * Created by Kun Yang on 16/3/12.
 */
public class CacheCapacitySupply implements InnerCapacitySupply {

    private CapacityCache cache;

    public CacheCapacitySupply(Item<?> item) {
        this(new DefaultCapacityCache(item));
    }

    public CacheCapacitySupply(long playerID, ItemModel model) {
        this(new DefaultCapacityCache(playerID, model));
    }

    public CacheCapacitySupply(Item<?> item, ItemModel model) {
        this(new DefaultCapacityCache(item, model));
    }

    public CacheCapacitySupply(CapacityCache cache) {
        this.cache = cache;
    }

    @Override
    public Number getValue(Capacity capacity) {
        return cache.get(capacity);
    }

    @Override
    public Number getValue(Capacity capacity, Number defaultNum) {
        return cache.get(capacity, defaultNum);
    }

    @Override
    public Map<Capacity, Number> getAllCapacityValue() {
        return cache.getAll(Capacity.class).entrySet()
                .stream()
                .collect(Collectors.toMap(
                        e -> (Capacity) e.getKey(),
                        Entry::getValue));
    }

    @Override
    public boolean isHasValue(Capacity capacity) {
        return cache.hasAbility(capacity);
    }

    @Override
    public long getPowerValue() {
        return 0;
    }

    // @Override
    // public Set<Capacity> getSupplyCapacities() {
    //     return cache.getAbilityTypes(Capacity.class);
    // }

    @Override
    public void refresh(CapacitySupplier supplier) {
        cache.refresh();
        CapacityEvents.ON_CHANGE.notify(this, supplier);
    }

    @Override
    public void invalid(CapacitySupplier supplier) {
        cache.refresh();
        CapacityEvents.ON_INVALID.notify(this, supplier);
    }

    @Override
    public void effect(CapacitySupplier supplier) {
        cache.refresh();
        CapacityEvents.ON_EFFECT.notify(this, supplier);
    }

}
