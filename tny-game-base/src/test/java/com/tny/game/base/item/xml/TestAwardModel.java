package com.tny.game.base.item.xml;

import com.tny.game.base.item.*;

public class TestAwardModel extends AbstractItemModel {

    public TestAwardModel(String alias) {
        this.alias = alias;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ItemType getItemType() {
        return new ItemType() {

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

        };
    }
}
