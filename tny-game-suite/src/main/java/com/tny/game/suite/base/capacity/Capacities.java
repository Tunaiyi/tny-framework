package com.tny.game.suite.base.capacity;

import com.google.common.collect.ImmutableSet;
import com.tny.game.common.reflect.ObjectUtils;
import com.tny.game.common.utils.collection.CopyOnWriteMap;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by Kun Yang on 2017/4/7.
 */
public class Capacities {

    private static final Map<CapacityGroup, Set<Capacity>> groupCapacities = new CopyOnWriteMap<>();

    static void register(Capacity capacity) {
        Set<Capacity> capacities = groupCapacities.get(capacity.getGroup());
        if (capacities == null) {
            capacities = new CopyOnWriteArraySet<>();
            capacities = ObjectUtils.ifNull(groupCapacities.putIfAbsent(capacity.getGroup(), capacities), capacities);
        }
        capacities.add(capacity);
    }

    public static Set<Capacity> getCapacities(CapacityGroup group) {
        Set<Capacity> capacities = groupCapacities.get(group);
        if (capacities == null)
            return ImmutableSet.of();
        return Collections.unmodifiableSet(capacities);
    }

}
