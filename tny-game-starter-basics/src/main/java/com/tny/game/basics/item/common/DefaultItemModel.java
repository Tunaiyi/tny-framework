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
