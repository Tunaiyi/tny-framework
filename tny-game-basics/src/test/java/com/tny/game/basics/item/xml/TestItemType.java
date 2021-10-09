package com.tny.game.basics.item.xml;

import com.tny.game.basics.item.*;

public enum TestItemType implements ItemType {

    PLAYER,

    //
    ;


    @Override
    public Integer getId() {
        return 0;
    }

    @Override
    public String getAliasHead() {
        return null;
    }

    @Override
    public String getDesc() {
        return null;
    }

}
