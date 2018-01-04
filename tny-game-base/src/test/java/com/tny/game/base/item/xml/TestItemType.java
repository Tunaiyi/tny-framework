package com.tny.game.base.item.xml;

import com.tny.game.base.item.ItemType;

public enum TestItemType implements ItemType {

    PLAYER,

    //
    ;


    @Override
    public Integer getID() {
        return 0;
    }

    @Override
    public String getAliasHead() {
        return null;
    }

    @Override
    public boolean hasEntity() {
        return false;
    }

    @Override
    public String getDesc() {
        return null;
    }

}
