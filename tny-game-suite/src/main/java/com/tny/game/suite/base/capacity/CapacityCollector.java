package com.tny.game.suite.base.capacity;

import com.tny.game.common.number.*;

import java.util.*;

/**
 * Created by Kun Yang on 2017/7/17.
 */
public class CapacityCollector {

    private Map<Capacity, Number> capacities = new HashMap<>();

    private Map<Capacity, Number> visitCapacities = Collections.unmodifiableMap(capacities);

    public void collect(Capacity capacity, Number number) {
        if (number == null) {
            return;
        }
        capacities.merge(capacity, number, NumberAide::add);
    }

    public Map<Capacity, Number> getCapacities() {
        return visitCapacities;
    }

}
