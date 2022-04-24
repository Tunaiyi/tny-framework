package com.tny.game.basics.item.capacity;

import java.util.*;

/**
 * 游戏能力值Service
 * Created by Kun Yang on 16/2/17.
 */
public class CapacityService {

    public void accept(BaseCapablerItem<?> goal, CapacitySupplier... suppliers) {
        this.accept(goal, Arrays.asList(suppliers));
    }

    public void accept(BaseCapablerItem<?> goal, Collection<CapacitySupplier> suppliers) {
        goal.accept(suppliers);
    }

    public void reduce(BaseCapablerItem<?> goal, CapacitySupplier... suppliers) {
        this.reduce(goal, Arrays.asList(suppliers));
    }

    public void reduce(BaseCapablerItem<?> goal, Collection<CapacitySupplier> suppliers) {
        goal.remove(suppliers);
    }

}
