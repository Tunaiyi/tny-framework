package com.tny.game.lifecycle;

/**
 * 初始化优先级
 * 优先级越高 越先初始化 10 -> 1
 *
 * @author KunYang
 */
public enum LifecycleLevel implements LifecyclePriority{

    LEVEL_1(1),

    LEVEL_2(2),

    LEVEL_3(3),

    LEVEL_4(4),

    LEVEL_5(5),

    LEVEL_6(6),

    LEVEL_7(7),

    LEVEL_8(8),

    LEVEL_9(9),

    LEVEL_10(10);

    public final int priority;

    LifecycleLevel(int priority) {
        this.priority = priority;
    }

    @Override
    public int getPriority() {
        return priority;
    }
}
