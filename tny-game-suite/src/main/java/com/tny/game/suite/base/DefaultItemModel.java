package com.tny.game.suite.base;

import com.tny.game.base.item.ItemType;
import com.tny.game.suite.base.capacity.XMLCapacityItemModel;

public class DefaultItemModel extends XMLCapacityItemModel {

    private ItemType itemType;

    @Override
    protected ItemType itemType() {
        return itemType;
    }

}
