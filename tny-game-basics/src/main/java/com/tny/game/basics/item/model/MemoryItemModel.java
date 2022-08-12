/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item.model;

import com.tny.game.basics.item.*;

/**
 * 抽象xml映射事物模型
 *
 * @author KGTny
 */
public abstract class MemoryItemModel extends BaseItemModel {

    private final ItemType itemType;

    protected MemoryItemModel(ItemType itemType, String alisa, String desc) {
        this.id = itemType.getId();
        this.itemType = itemType;
        this.alias = itemType.alisaOf(alisa);
        this.desc = desc;
    }

    @Override
    public ItemType itemType() {
        return itemType;
    }

}
