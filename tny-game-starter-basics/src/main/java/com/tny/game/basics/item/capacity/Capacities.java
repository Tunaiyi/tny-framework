/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item.capacity;

import com.google.common.collect.ImmutableSet;
import com.tny.game.common.concurrent.collection.*;
import com.tny.game.common.utils.*;

import java.util.*;
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
        groupMap.put(group.getId(), group);
        capacities.add(capacity);
    }

    static void register(CapacityGroup group) {
        Set<Capacity> capacities = groupCapacities.get(group);
        if (capacities == null) {
            groupCapacities.putIfAbsent(group, new CopyOnWriteArraySet<>());
        }
        groupMap.put(group.getId(), group);
    }

    public static Set<Capacity> getCapacities(CapacityGroup group) {
        Set<Capacity> capacities = groupCapacities.get(group);
        if (capacities == null) {
            return ImmutableSet.of();
        }
        return Collections.unmodifiableSet(capacities);
    }

    public static CapacityGroup getGroup(int id) {
        return Asserts.checkNotNull(groupMap.get(id), "CapacityGroup [{}] is not exist", id);
    }

}
