package com.tny.game.base.item.xml;


import com.tny.game.base.item.*;

@SuppressWarnings("unchecked")
public class TestItemModelImpl extends XMLItemModel implements TestItemModel {

    @Override
    protected ItemType itemType() {
        return TestItemType.PLAYER;
    }
}
