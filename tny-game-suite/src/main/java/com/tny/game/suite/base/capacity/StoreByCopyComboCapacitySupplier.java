package com.tny.game.suite.base.capacity;

import com.google.common.base.MoreObjects;
import com.google.common.collect.*;
import com.tny.game.basics.item.*;
import com.tny.game.common.number.*;

import java.util.*;
import java.util.stream.*;

/**
 * 游戏能力值提供器
 * Created by Kun Yang on 16/2/15.
 */
public class StoreByCopyComboCapacitySupplier extends BaseStoreCapacitiable implements ComboCapacitySupplier, StoreCapacitySupplier {

    private long id;

    private int modelId;

    private CapacitySupplierType type;

    private Set<Long> suppliers;

    private CapacityVisitor visitor;

    private Set<CapacityGroup> groups;

    StoreByCopyComboCapacitySupplier(CapacitySupplierType type, long id, int modelId, Stream<Long> suppliers, Stream<CapacityGroup> groups,
            CapacityVisitor visitor, long expireAt) {
        super(expireAt);
        this.id = id;
        this.modelId = modelId;
        this.type = type;
        ImmutableSet.Builder<Long> suppliersBuilder = ImmutableSet.builder();
        suppliers.forEach(suppliersBuilder::add);
        this.suppliers = suppliersBuilder.build();
        ImmutableSet.Builder<CapacityGroup> groupsBuilder = ImmutableSet.builder();
        groups.forEach(groupsBuilder::add);
        this.groups = groupsBuilder.build();
        this.visitor = visitor;
    }

    StoreByCopyComboCapacitySupplier(CapacitySupplierType type, long id, int modelId, Stream<? extends CapacitySupplier> suppliers,
            CapacityVisitor visitor, long expireAt) {
        super(expireAt);
        this.id = id;
        this.modelId = modelId;
        this.type = type;
        ImmutableSet.Builder<Long> suppliersBuilder = ImmutableSet.builder();
        ImmutableSet.Builder<CapacityGroup> groupsBuilder = ImmutableSet.builder();
        suppliers.forEach(s -> {
            suppliersBuilder.add(s.getId());
            groupsBuilder.addAll(s.getAllCapacityGroups());
        });
        this.suppliers = suppliersBuilder.build();
        this.groups = groupsBuilder.build();
        this.visitor = visitor;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public int getModelId() {
        return modelId;
    }

    @Override
    public long getPlayerId() {
        return visitor.getPlayerId();
    }

    @Override
    public CapacitySupplierType getSupplierType() {
        return type;
    }

    @Override
    public Collection<? extends CapacitySupplier> dependSuppliers() {
        if (suppliers.isEmpty()) {
            return ImmutableList.of();
        }
        return suppliers.stream()
                .map(visitor::findSupplier)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public Stream<? extends CapacitySupplier> dependSuppliersStream() {
        if (suppliers.isEmpty()) {
            return Stream.empty();
        }
        return suppliers.stream()
                .map(visitor::findSupplier)
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    @Override
    public boolean isHasValue(Capacity capacity) {
        if (!this.isSupplying() || suppliers.isEmpty()) {
            return false;
        }
        return suppliers.stream()
                .map(visitor::findSupplier)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .anyMatch(s -> s.isHasValue(capacity));
    }

    @Override
    public Number getValue(Capacity capacity, Number defaultValue) {
        if (!this.isSupplying() || suppliers.isEmpty()) {
            return defaultValue;
        }
        return suppliers.stream()
                .map(visitor::findSupplier)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(s -> s.getValue(capacity))
                .filter(Objects::nonNull)
                .reduce(NumberAide::add)
                .orElse(defaultValue);
    }

    @Override
    public Set<CapacityGroup> getAllCapacityGroups() {
        if (!this.isSupplying() || suppliers.isEmpty()) {
            return ImmutableSet.of();
        }
        return groups;
    }

    @Override
    public Number getValue(Capacity capacity) {
        return getValue(capacity, null);
    }

    @Override
    public Map<Capacity, Number> getAllValues() {
        if (!this.isSupplying() || suppliers.isEmpty()) {
            return ImmutableMap.of();
        }
        Map<Capacity, Number> numberMap = new HashMap<>();
        suppliers.stream()
                .map(visitor::findSupplier)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(s -> s.getAllValues().forEach((c, num) -> numberMap.merge(c, num, NumberAide::add)));
        return numberMap;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("itemId", modelId)
                .add("name", ItemModels.name(modelId))
                .add("suppliers", suppliers)
                .toString();
    }

}