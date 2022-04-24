package com.tny.game.basics.item.capacity;

import java.util.*;

/**
 * 内部能力值作用对象
 * Created by Kun Yang on 16/2/15.
 */
public interface CompositionCapabler extends Capabler {

    CapableComposition composition();

    @Override
    default Collection<? extends CapacitySupplier> suppliers() {
        return composition().suppliers();
    }

    @Override
    default Set<CapacityGroup> getAllCapacityGroups() {
        return composition().getAllCapacityGroups();
    }

}
