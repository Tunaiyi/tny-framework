package com.tny.game.basics.item.xml;


import com.tny.game.basics.item.*;

@SuppressWarnings("unchecked")
public class TestItemModelImpl extends XMLItemModel implements TestItemModel {

    @Override
    protected ItemType itemType() {
        return TestItemType.PLAYER;
    }
}
