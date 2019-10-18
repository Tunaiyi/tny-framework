package com.tny.game.base.item.xml;

import com.tny.game.base.item.behavior.DemandType;
import com.tny.game.common.result.ResultCode;

public enum TestDemandType implements DemandType {

    PLAYER_LEVEL,

    PLAYER_ONLINE;

    @Override
    public ResultCode getResultCode() {
        return null;
    }

    @Override
    public boolean isCost() {
        return false;
    }

    @Override
    public Integer getId() {
        return 0;
    }

}
