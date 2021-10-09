package com.tny.game.basics.item.capacity;

import java.util.Collection;

/**
 * 内部能力值作用对象
 * Created by Kun Yang on 16/2/15.
 */
public interface InnerCapacityGather extends CapacityGather {

    /**
     * 接受指定的 supplier
     *
     * @param supplier 指定的 supplier
     */
    default boolean accept(CapacitySupplier supplier) {
        return this.doAccept(supplier);
    }

    default boolean accept(Collection<? extends CapacitySupplier> suppliers) {
        boolean acc = false;
        for (CapacitySupplier supplier : suppliers) {
            if (this.doAccept(supplier))
                acc = true;
        }
        return acc;
    }

    /**
     * 移除指定的 supplier
     *
     * @param supplier 指定的 supplier
     */
    default boolean reduce(CapacitySupplier supplier) {
        return this.doReduce(supplier);
    }

    default boolean reduce(Collection<? extends CapacitySupplier> suppliers) {
        return suppliers.stream().anyMatch(this::doReduce);
    }

    boolean doAccept(CapacitySupplier supplier);

    boolean doReduce(CapacitySupplier supplier);

    void clear();

}
