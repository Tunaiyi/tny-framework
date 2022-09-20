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
package com.tny.game.common.lifecycle;

import com.tny.game.common.utils.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/8/6 02:23
 **/
public final class LifecyclePriorities {

    private static final Map<Integer, LifecyclePriority> priorities = new ConcurrentHashMap<>();

    static {
        for (LifecycleLevel level : LifecycleLevel.values()) {
            priorities.put(level.getOrder(), level);
        }
    }

    private LifecyclePriorities() {
    }

    public static LifecyclePriority of(int priority) {
        return priorities.computeIfAbsent(priority, CustomLifecyclePriority::new);
    }

    public static LifecyclePriority highest() {
        return of(Integer.MAX_VALUE);
    }

    public static LifecyclePriority lowest() {
        return of(1);
    }

    public static LifecyclePriority lower(LifecyclePriority priority, int order) {
        Asserts.checkArgument(order > 0, "order {} must > 0", order);
        var priorityValue = priority.getOrder() - order;
        Asserts.checkArgument(priorityValue >= 0, "{} is lowest", priorityValue);
        return of(priorityValue);
    }

    public static LifecyclePriority lower(LifecyclePriority priority) {
        return lower(priority, 1);
    }

    public static LifecyclePriority higher(LifecyclePriority priority, int order) {
        Asserts.checkArgument(order > 0, "order {} must > 0", order);
        var priorityValue = priority.getOrder() + order;
        Asserts.checkArgument(priorityValue < Integer.MAX_VALUE, "order {} must <= {}", priorityValue, Integer.MAX_VALUE);
        return of(priorityValue);
    }

    public static LifecyclePriority higher(LifecyclePriority priority) {
        return higher(priority, 1);
    }

    private static class CustomLifecyclePriority implements LifecyclePriority {

        private final int priority;

        private CustomLifecyclePriority(int priority) {
            this.priority = priority;
        }

        @Override
        public int getOrder() {
            return priority;
        }

    }

}
