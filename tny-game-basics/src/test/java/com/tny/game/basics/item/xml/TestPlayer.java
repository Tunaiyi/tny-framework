/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item.xml;

import com.tny.game.basics.item.*;

public class TestPlayer extends BaseItem<ItemModel> {

    private int number;

    private int level;

    private String itemAlias;

    public TestPlayer(int number, int level, String alias) {
        super();
        this.number = number;
        this.level = level;
        this.itemAlias = alias;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setItemAlias(String itemAlias) {
        this.itemAlias = itemAlias;
    }

    public int getNumber() {
        return number;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public long getId() {
        return 0;
    }

    @Override
    public String getAlias() {
        return itemAlias;
    }

}