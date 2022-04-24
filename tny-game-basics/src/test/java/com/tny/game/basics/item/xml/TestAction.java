package com.tny.game.basics.item.xml;

import com.tny.game.basics.item.behavior.*;
import com.tny.game.basics.mould.*;

public enum TestAction implements Action {

    NOMAL_UPGRADE,

    GOLD_UPGRADE;

    @Override
    public int id() {
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
