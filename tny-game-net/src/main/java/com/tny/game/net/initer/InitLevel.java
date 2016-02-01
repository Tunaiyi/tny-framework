package com.tny.game.net.initer;

/**
 * 初始化优先级
 * 优先级越高 越先初始化 10 -> 1
 *
 * @author KunYang
 */
public enum InitLevel {

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

    InitLevel(int priority) {
        this.priority = priority;
    }

}
