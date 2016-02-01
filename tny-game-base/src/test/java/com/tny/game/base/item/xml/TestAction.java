package com.tny.game.base.item.xml;

import com.tny.game.base.item.behavior.Action;
import com.tny.game.base.item.behavior.Behavior;
import com.tny.game.base.module.Feature;

public enum TestAction implements Action {

    NOMAL_UPGRADE,

    GOLD_UPGRADE;

    @Override
    public Integer getID() {
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
