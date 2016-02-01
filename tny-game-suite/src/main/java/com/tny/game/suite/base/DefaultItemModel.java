package com.tny.game.suite.base;

import com.tny.game.base.item.ItemType;
import com.tny.game.base.item.xml.XMLItemModel;

public class DefaultItemModel extends XMLItemModel {

    private ItemType itemType;

    @Override
    protected ItemType itemType() {
        return itemType;
    }

}
