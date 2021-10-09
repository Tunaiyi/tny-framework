package com.tny.game.basics.item.xml;

import com.tny.game.basics.item.behavior.*;
import com.tny.game.basics.module.*;

public enum TestBehavior implements Behavior {

    UPGRATE;

    @Override
    public Integer getId() {
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
