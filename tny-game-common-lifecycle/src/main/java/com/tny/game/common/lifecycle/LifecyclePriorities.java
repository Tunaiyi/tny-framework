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
            priorities.put(level.getPriority(), level);
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
        return of(0);
    }

    public static LifecyclePriority lower(LifecyclePriority priority, int num) {
        Asserts.checkArgument(num > 0, "num {} must > 0", num);
        var priorityValue = priority.getPriority() - num;
        Asserts.checkArgument(priorityValue >= 0, "{} is lowest", priorityValue);
        return of(priorityValue);
    }

    public static LifecyclePriority lower(LifecyclePriority priority) {
        return lower(priority, 1);
    }

    public static LifecyclePriority higher(LifecyclePriority priority, int num) {
        Asserts.checkArgument(num > 0, "num {} must > 0", num);
        long priorityValue = (long)priority.getPriority() + num;
        Asserts.checkArgument(priorityValue < Integer.MAX_VALUE, "num {} must <= {}", priorityValue, Integer.MAX_VALUE);
        return of((int)priorityValue);
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
        public int getPriority() {
            return priority;
        }

    }

}
