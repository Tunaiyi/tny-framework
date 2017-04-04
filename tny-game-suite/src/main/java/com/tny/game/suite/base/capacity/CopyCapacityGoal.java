package com.tny.game.suite.base.capacity;

import com.tny.game.common.utils.collection.CopyOnWriteMap;

import java.util.Collection;
import java.util.Map;

/**
 * 复制的能力提供器
 * Created by Kun Yang on 16/3/11.
 */
public class CopyCapacityGoal extends InnerCapacityGoal {

    private Map<SupplierKey, CapacitySupplier> supplierMap = new CopyOnWriteMap<>();

    private CapacityGoalType goalType;

    public CopyCapacityGoal(CapacityGoalType goalType) {
        this.goalType = goalType;
    }

    @Override
    protected boolean doAccept(CapacitySupplier supplier) {
        if (!supplier.isCanJoinTo(this.goalType))
            return false;
        synchronized (supplier) {
            SupplierKey key = new SupplierKey(supplier);
            boolean contains = false;
            if (supplierMap.containsKey(key))
                contains = true;
            if (!(supplier instanceof ImmutableCapacitySupplier) && isBeImmutable(supplier))
                supplier = new ImmutableCapacitySupplier(supplier);
            this.putSupplier(key, supplier);
            return !contains;
        }
    }

    @Override
    protected boolean doReduce(CapacitySupplier supplier) {
        synchronized (supplier) {
            SupplierKey key = new SupplierKey(supplier);
            return supplierMap.remove(key) != null;
        }
    }

    @Override
    public void clear() {
        this.supplierMap = new CopyOnWriteMap<>();
    }

    protected boolean isBeImmutable(CapacitySupplier supplier) {
        return true;
    }

    @Override
    public CapacityGoalType getGoalType() {
        return goalType;
    }

    @Override
    public Collection<CapacitySupplier> suppliers() {
        return this.supplierMap.values();
    }

    private boolean putSupplier(SupplierKey key, CapacitySupplier supplier) {
        if (supplier.isCanJoinTo(this.goalType)) {
            this.supplierMap.put(key, supplier);
            return true;
        }
        return false;
    }

    public boolean acceptWithoutCopy(CapacitySupplier supplier) {
        synchronized (supplier) {
            return putSupplier(new SupplierKey(supplier), supplier);
        }
    }

    private static class SupplierKey {
        private long id;
        private int itemID;
        private long playerID;

        private SupplierKey(CapacitySupplier supplier) {
            this.id = supplier.getID();
            this.itemID = supplier.getItemID();
            this.playerID = supplier.getPlayerID();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SupplierKey key = (SupplierKey) o;

            if (playerID != key.playerID) return false;
            if (itemID != key.itemID) return false;
            return id == key.id;

        }

        @Override
        public int hashCode() {
            int result = (int) (id ^ (id >>> 32));
            result = 31 * result + itemID;
            result = 31 * result + (int) (playerID ^ (playerID >>> 32));
            return result;
        }
    }
}

