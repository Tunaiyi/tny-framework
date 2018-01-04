package com.tny.game.base.item.xml;

import com.tny.game.base.item.AbstractItemModel;
import com.tny.game.base.item.ItemType;

public class TestAwardModel extends AbstractItemModel {

    public TestAwardModel(String alias) {
        this.alias = alias;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <IT extends ItemType> IT getItemType() {
        return (IT) new ItemType() {

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

        };
    }
}
