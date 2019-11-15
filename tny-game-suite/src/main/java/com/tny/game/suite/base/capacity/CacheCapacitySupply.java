package com.tny.game.suite.base.capacity;

import com.tny.game.base.item.*;
import com.tny.game.suite.base.capacity.event.*;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * 可缓存能力提供器
 * Created by Kun Yang on 16/3/12.
 */
public class CacheCapacitySupply implements InnerCapacitySupply {

    private AbilitiesCache<? extends CapacityItemModel> cache;

    public CacheCapacitySupply(Item<? extends CapacityItemModel> item) {
        this(new DefaultAbilitiesCache<>(item));
    }

    public CacheCapacitySupply(long playerID, CapacityItemModel model) {
        this(new DefaultAbilitiesCache<>(playerID, model));
    }

    public CacheCapacitySupply(Item<?> item, CapacityItemModel model) {
        this(new DefaultAbilitiesCache<>(item, model));
    }

    private CacheCapacitySupply(AbilitiesCache<? extends CapacityItemModel> cache) {
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
    public void collectValues(CapacityCollector collector, Collection<? extends Capacity> capacities) {
        for (Capacity capacity : capacities) {
            collector.collect(capacity, cache.get(capacity));
        }
    }

    @Override
    public Set<CapacityGroup> getAllCapacityGroups() {
        return this.cache.itemModel().getCapacityGroups();
    }

    @Override
    public Map<Capacity, Number> getAllValues() {
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
