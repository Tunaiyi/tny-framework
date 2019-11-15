package com.tny.game.net.transport;

import com.tny.game.common.enums.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-08 11:49
 */
public enum TunnelState implements EnumIdentifiable<Integer> {


    /**
     * 初始化
     **/
    INIT(1),

    /**
     * 激活
     **/
    ACTIVATE(2),

    /**
     * 未激活
     */
    UNACTIVATED(3),

    /**
     * 关闭
     **/
    CLOSE(4);

    //

    private int id;

    TunnelState(int id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }


}
