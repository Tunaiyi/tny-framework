package com.tny.game.common.utils.capacity;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 内部能力值作用对象
 * Created by Kun Yang on 16/2/15.
 */
public abstract class AbstractCapacityGather implements InnerCapacityGather {

    protected volatile Set<CapacitySupplier> suppliers = new CopyOnWriteArraySet<>();

    @Override
    public Collection<? extends CapacitySupplier> suppliers() {
        return Collections.unmodifiableCollection(suppliers);
    }

    @Override
    public void clear() {
        this.suppliers = new CopyOnWriteArraySet<>();
    }

    @Override
    public void collectCapacities(CapacityCollector collector, Collection<? extends Capacity> capacities) {
        suppliers.forEach(supplier -> supplier.collectValues(collector, capacities));
    }

}
