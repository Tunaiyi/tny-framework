package com.tny.game.basics.item.capacity;

import com.tny.game.basics.item.*;

import java.util.Collection;

/**
 * 内部能力值作用对象
 * Created by Kun Yang on 16/2/15.
 */
public abstract class BaseCapablerItem<IM extends ItemModel> extends BaseItem<IM> implements Capabler {

    protected BaseCapablerItem() {
    }

    protected BaseCapablerItem(long playerId, IM model) {
        super(playerId, model);
    }

    protected abstract void accept(CapacitySupplier supplier);

    protected abstract void accept(Collection<? extends CapacitySupplier> suppliers);

    protected abstract void remove(CapacitySupplier supplier);

    protected abstract void remove(Collection<CapacitySupplier> suppliers);

    protected abstract void clear();

    protected abstract void invalid();

    protected abstract void effect();

}
