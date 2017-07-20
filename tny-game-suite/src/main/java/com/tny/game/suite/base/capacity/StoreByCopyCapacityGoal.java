package com.tny.game.suite.base.capacity;


import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.tny.game.base.item.ItemModels;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 游戏能力值提供器
 * Created by Kun Yang on 16/2/15.
 */
public class StoreByCopyCapacityGoal extends BaseStoreCapacitiable implements StoreCapacityGoal {

    private long id;

    private int itemID;

    private Set<Long> suppliers;

    private CapacityVisitor visitor;

    StoreByCopyCapacityGoal(long id, int itemID, Stream<Long> suppliers, CapacityVisitor visitor, long expireAt) {
        super(expireAt);
        this.id = id;
        this.itemID = itemID;
        ImmutableSet.Builder<Long> suppliersBuilder = ImmutableSet.builder();
        suppliers.forEach(suppliersBuilder::add);
        this.suppliers = suppliersBuilder.build();
        this.visitor = visitor;
    }

    @Override
    public long getID() {
        return id;
    }

    @Override
    public int getItemID() {
        return itemID;
    }

    @Override
    public long getPlayerID() {
        return visitor.getPlayerID();
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
                .add("itemID", itemID)
                .add("name", ItemModels.name(itemID))
                .add("suppliers", suppliers)
                .toString();
    }
}