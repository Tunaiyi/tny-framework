package com.tny.game.suite.base.capacity;

import com.tny.game.base.item.Item;
import com.tny.game.base.item.ItemModel;
import com.tny.game.suite.base.GameItem;

/**
 * Item能力值提供器
 * Created by Kun Yang on 16/3/12.
 */
public abstract class CacheCapacitySupplierItem<IM extends ItemModel> extends GameItem<IM> implements ProxyCapacitySupplier {

    protected CacheCapacitySupply capacitySupply;

    @Override
    protected void setModel(IM model) {
        super.setModel(model);
        this.capacitySupply = new CacheCapacitySupply(this);
    }

    @Override
    public Item<?> item() {
        return this;
    }

    @Override
    public CapacitySupply supply() {
        return capacitySupply;
    }

    @Override
    public abstract long getID();

    protected void refresh() {
        this.capacitySupply.refresh(this);
    }

}
