package com.tny.game.basics.item.xml;

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.model.*;

@SuppressWarnings("unchecked")
public class TestItemModelImpl extends BaseItemModel implements TestItemModel {

    @Override
    protected ItemType itemType() {
        return TestItemType.PLAYER;
    }

}
