package com.tny.game.suite.base.capacity;

import com.google.common.collect.ImmutableSet;
import com.tny.game.common.utils.ObjectAide;
import com.tny.game.common.collection.CopyOnWriteMap;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by Kun Yang on 2017/4/7.
 */
public class Capacities {

    private static final Map<CapacityGroup, Set<Capacity>> groupCapacities = new CopyOnWriteMap<>();
    private static final Map<Integer, CapacityGroup> groupMap = new CopyOnWriteMap<>();

    static void register(Capacity capacity) {
        Set<Capacity> capacities = groupCapacities.get(capacity.getGroup());
        if (capacities == null) {
            capacities = new CopyOnWriteArraySet<>();
            capacities = ObjectAide.ifNull(groupCapacities.putIfAbsent(capacity.getGroup(), capacities), capacities);
        }
        CapacityGroup group = capacity.getGroup();
        groupMap.put(group.getID(), group);
        capacities.add(capacity);
    }

    public static Set<Capacity> getCapacities(CapacityGroup group) {
        Set<Capacity> capacities = groupCapacities.get(group);
        if (capacities == null)
            return ImmutableSet.of();
        return Collections.unmodifiableSet(capacities);
    }


    public static CapacityGroup getGroup(int id) {
        return groupMap.get(id);
    }



}
