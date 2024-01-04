/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item.xml;

import com.tny.game.basics.item.*;

public class TestAwardModel extends AbstractItemModel {

    public TestAwardModel(String alias) {
        this.alias = alias;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ItemType getItemType() {
        return new ItemType() {

            @Override
            public String name() {
                return "def";
            }

            @Override
            public int id() {
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
