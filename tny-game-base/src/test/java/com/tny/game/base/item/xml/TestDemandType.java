package com.tny.game.base.item.xml;

import com.tny.game.base.item.behavior.*;
import com.tny.game.common.result.*;

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
