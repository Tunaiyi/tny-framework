package com.tny.game.net.transport;

import com.tny.game.common.enums.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-08 11:49
 */
public enum TunnelStatus implements IntEnumerable {

    /**
     * 初始化
     **/
    INIT(1),

    /**
     * 连接
     **/
    OPEN(2),

    /**
     * 挂起
     */
    SUSPEND(3),

    /**
     * 关闭
     **/
    CLOSED(4);

    //

    private final int id;

    TunnelStatus(int id) {
        this.id = id;
    }

    @Override
    public int id() {
        return this.id;
    }

}
