package com.tny.game.common.utils.capacity;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;

/**
 * 游戏能力值Service
 * Created by Kun Yang on 16/2/17.
 */
@Component
public class CapacityService {

    public void accept(CapacityGoalItem<?> goal, CapacitySupplier... suppliers) {
        this.accept(goal, Arrays.asList(suppliers));
    }

    public void accept(CapacityGoalItem<?> goal, Collection<CapacitySupplier> suppliers) {
        goal.accept(suppliers);
    }

    public void reduce(CapacityGoalItem<?> goal, CapacitySupplier... suppliers) {
        this.reduce(goal, Arrays.asList(suppliers));
    }

    public void reduce(CapacityGoalItem<?> goal, Collection<CapacitySupplier> suppliers) {
        goal.reduce(suppliers);
    }

}
