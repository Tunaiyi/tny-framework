package com.tny.game.base.item.xml;

import com.tny.game.base.item.behavior.Action;
import com.tny.game.base.item.behavior.Behavior;
import com.tny.game.base.module.Feature;

public enum TestBehavior implements Behavior {

    UPGRATE;

    @Override
    public Integer getID() {
        return 0;
    }

    @Override
    public Feature getFeature() {
        return null;
    }

    @Override
    public String getDesc() {
        return null;
    }

    @Override
    public Action forAction(Object value) {
        return null;
    }

}
