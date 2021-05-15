package com.tny.game.net.passthrough.event;

import com.tny.game.net.passthrough.*;

/**
 * 事件
 * <p>
 *
 * @author : kgtny
 * @date : 2021/3/4 8:23 下午
 */
public abstract class BaseTubulePipeEvent<UID> implements TubulePipeEvent<UID> {

    private final long tunnelId;

    private final UID userId;

    private final String userType;

    public BaseTubulePipeEvent(Tubule<UID> tubule) {
        this.tunnelId = tubule.getId();
        this.userId = tubule.getUserId();
        this.userType = tubule.getUserType();
    }

    @Override
    public long getTunnelId() {
        return this.tunnelId;
    }

    @Override
    public UID getUserId() {
        return this.userId;
    }

    @Override
    public String getUserType() {
        return this.userType;
    }

}
