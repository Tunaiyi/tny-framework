package com.tny.game.suite.base.capacity;


import com.google.common.base.MoreObjects;
import com.google.common.collect.*;
import com.tny.game.base.item.*;

import java.util.*;
import java.util.stream.*;

/**
 * 游戏能力值提供器
 * Created by Kun Yang on 16/2/15.
 */
public class StoreByCopyCapacityGoal extends BaseStoreCapacitiable implements StoreCapacityGoal {

    private long id;

    private int itemID;

    private Set<Long> suppliers;

    private Set<CapacityGroup> groups;

    private CapacityVisitor visitor;

    StoreByCopyCapacityGoal(long id, int itemID, Stream<Long> suppliers, Stream<CapacityGroup> groups, CapacityVisitor visitor, long expireAt) {
        super(expireAt);
        this.id = id;
        this.itemID = itemID;
        ImmutableSet.Builder<Long> suppliersBuilder = ImmutableSet.builder();
        suppliers.forEach(suppliersBuilder::add);
        this.suppliers = suppliersBuilder.build();
        ImmutableSet.Builder<CapacityGroup> groupsBuilder = ImmutableSet.builder();
        groups.forEach(groupsBuilder::add);
        this.groups = groupsBuilder.build();
        this.visitor = visitor;
    }

    StoreByCopyCapacityGoal(long id, int itemID, Stream<? extends CapacitySupplier> suppliers, CapacityVisitor visitor, long expireAt) {
        super(expireAt);
        this.id = id;
        this.itemID = itemID;
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
    public int getItemId() {
        return itemID;
    }

    @Override
    public long getPlayerId() {
        return visitor.getPlayerId();
    }

    @Override
    public Set<CapacityGroup> getSuppliersCapacityGroups() {
        return groups;
    }

    @Override
    public Collection<? extends CapacitySupplier> suppliers() {
        if (suppliers.isEmpty())
            return ImmutableList.of();
        return suppliers.stream()
                        .map(visitor::findSupplier)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList());
    }

    @Override
    public Stream<? extends CapacitySupplier> suppliersStream() {
        if (suppliers.isEmpty())
            return Stream.empty();
        return suppliers.stream()
                        .map(visitor::findSupplier)
                        .filter(Optional::isPresent)
                        .map(Optional::get);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("id", id)
                          .add("itemId", itemID)
                          .add("name", ItemModels.name(itemID))
                          .add("suppliers", suppliers)
                          .toString();
    }
}