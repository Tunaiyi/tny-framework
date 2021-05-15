package com.tny.game.net.passthrough.event;

import com.tny.game.common.enums.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/3/1 3:04 上午
 */
public enum PipeEventType implements EnumIdentifiable<Integer> {

    /**
     * 数据事件
     */
    MESSAGE(1),

    /**
     * 通道事件
     */
    PIPE(2),

    /**
     * 子通道事件
     */
    TUBULE(3),

    ;

    private final int id;

    PipeEventType(int id) {
        this.id = id;
        this.checkEnum();
    }

    @Override
    public Integer getId() {
        return this.id;
    }

}
