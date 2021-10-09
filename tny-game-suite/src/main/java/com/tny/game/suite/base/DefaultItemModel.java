package com.tny.game.suite.base;

import com.tny.game.basics.item.*;
import com.tny.game.suite.base.capacity.*;

public class DefaultItemModel extends XMLCapacityItemModel {

    private ItemType itemType;

    @Override
    protected ItemType itemType() {
        return itemType;
    }

}
