package com.tny.game.common.utils;

import com.tny.game.base.item.ItemType;
import com.tny.game.common.utils.capacity.XMLCapacityItemModel;

public class DefaultItemModel extends XMLCapacityItemModel {

    private ItemType itemType;

    @Override
    protected ItemType itemType() {
        return itemType;
    }

}
