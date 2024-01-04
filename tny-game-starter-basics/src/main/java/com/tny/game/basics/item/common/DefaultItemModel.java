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

package com.tny.game.basics.item.common;

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.capacity.*;

public class DefaultItemModel extends BaseCapacitySupplierItemModel {

    private ItemType itemType;

    private CapacitySupplierType supplierType;

    @Override
    protected ItemType itemType() {
        return itemType;
    }

    @Override
    public CapacitySupplierType getSupplierType() {
        if (supplierType == null) {
            return CapacitySupplierType.NONE;
        }
        return supplierType;
    }

}
