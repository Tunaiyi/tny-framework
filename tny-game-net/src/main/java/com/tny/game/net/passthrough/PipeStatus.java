package com.tny.game.net.passthrough;

import com.tny.game.common.enums.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/3/2 3:36 下午
 */
public enum PipeStatus implements EnumIdentifiable<Integer> {

    /**
     * 初始化
     **/
    INIT(1, false),

    /**
     * 激活
     **/
    ACTIVATED(2, false),

    /**
     * 未激活
     */
    UNACTIVATED(3, false),

    /**
     * 关闭中
     */
    CLOSING(4, true),

    /**
     * 关闭
     **/
    CLOSED(5, true);

    //
    ;

    private final int id;

    private final boolean closeStatus;

    PipeStatus(int id, boolean closeStatus) {
        this.id = id;
        this.closeStatus = closeStatus;
    }

    @Override
    public Integer getId() {
        return this.id;
    }

    public boolean isCloseStatus() {
        return this.closeStatus;
    }
}
