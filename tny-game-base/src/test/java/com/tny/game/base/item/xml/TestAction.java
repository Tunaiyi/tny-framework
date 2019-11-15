package com.tny.game.base.item.xml;

import com.tny.game.base.item.behavior.*;
import com.tny.game.base.module.*;

public enum TestAction implements Action {

    NOMAL_UPGRADE,

    GOLD_UPGRADE;

    @Override
    public Integer getId() {
        return 0;
    }

    @Override
    public Behavior getBehavior() {
        return null;
    }

    @Override
    public Feature getFeature() {
        return null;
    }

    @Override
    public String getDesc() {
        return null;
    }

}
