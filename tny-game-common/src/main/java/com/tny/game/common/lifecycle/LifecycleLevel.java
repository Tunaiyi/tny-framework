package com.tny.game.common.lifecycle;

/**
 * 初始化优先级
 * 优先级越高 越先初始化 10 -> 1
 *
 * @author KunYang
 */
public enum LifecycleLevel implements LifecyclePriority{

    SYSTEM_LEVEL_1(9001),

    SYSTEM_LEVEL_2(9002),

    SYSTEM_LEVEL_3(9003),

    SYSTEM_LEVEL_4(9004),

    SYSTEM_LEVEL_5(9005),

    SYSTEM_LEVEL_6(9006),

    SYSTEM_LEVEL_7(9007),

    SYSTEM_LEVEL_8(9008),

    SYSTEM_LEVEL_9(9009),

    SYSTEM_LEVEL_10(9010),

    CUSTOM_LEVEL_1(1001),

    CUSTOM_LEVEL_2(1002),

    CUSTOM_LEVEL_3(1003),

    CUSTOM_LEVEL_4(1004),

    CUSTOM_LEVEL_5(1005),

    CUSTOM_LEVEL_6(1006),

    CUSTOM_LEVEL_7(1007),

    CUSTOM_LEVEL_8(1008),

    CUSTOM_LEVEL_9(1009),

    CUSTOM_LEVEL_10(1010),

    POST_SYSTEM_LEVEL_1(101),

    POST_SYSTEM_LEVEL_2(102),

    POST_SYSTEM_LEVEL_3(103),

    POST_SYSTEM_LEVEL_4(104),

    POST_SYSTEM_LEVEL_5(105),

    POST_SYSTEM_LEVEL_6(106),

    POST_SYSTEM_LEVEL_7(107),

    POST_SYSTEM_LEVEL_8(108),

    POST_SYSTEM_LEVEL_9(109),

    POST_SYSTEM_LEVEL_10(110),
    ;

    public final int priority;

    LifecycleLevel(int priority) {
        this.priority = priority;
    }

    @Override
    public int getPriority() {
        return priority;
    }
}
