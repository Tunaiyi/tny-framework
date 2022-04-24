package com.tny.game.basics.item.capacity;

import com.tny.game.scanner.*;
import com.tny.game.scanner.annotation.*;

import static com.tny.game.scanner.selector.EnumClassSelector.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/29 5:25 下午
 */
public class CapacityEnumClassLoader {

    @ClassSelectorProvider
    static ClassSelector capacitiesSelector() {
        return createSelector(Capacity.class, Capacities::register);
    }

    @ClassSelectorProvider
    static ClassSelector capacityGroupSelector() {
        return createSelector(CapacityGroup.class, Capacities::register);
    }

}
