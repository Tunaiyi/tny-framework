package com.tny.game.net.transport;

import com.tny.game.common.enums.EnumID;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-08 11:49
 */
public enum TunnelState implements EnumID<Integer> {


    /**
     * 初始化
     **/
    INIT(1),

    /**
     * 存活
     **/
    ALIVE(2),

    /**
     * 不可以
     */
    UNALIVE(3),

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
    public Integer getID() {
        return id;
    }



}
