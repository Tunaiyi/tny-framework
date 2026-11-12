/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
        // lowest()==1，0 不是合法档位（原 >=0 放行产出低于 lowest 的 0 级）
        Asserts.checkArgument(priorityValue >= 1, "{} is lowest", priority.getOrder());
        return of(priorityValue);
    }

    public static LifecyclePriority lower(LifecyclePriority priority) {
        return lower(priority, 1);
    }

    public static LifecyclePriority higher(LifecyclePriority priority, int order) {
        Asserts.checkArgument(order > 0, "order {} must > 0", order);
        // 原先加后检：highest()+1 溢出为 MIN_VALUE（负数），检查放行后产出"最后执行"的假最高级
        Asserts.checkArgument(priority.getOrder() <= Integer.MAX_VALUE - order,
                "{} 已是最高，无法更高", priority.getOrder());
        return of(priority.getOrder() + order);
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
