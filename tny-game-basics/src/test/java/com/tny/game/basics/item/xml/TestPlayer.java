/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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