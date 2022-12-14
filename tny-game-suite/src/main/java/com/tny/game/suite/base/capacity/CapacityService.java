package com.tny.game.suite.base.capacity;

import org.slf4j.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 游戏能力值Service
 * Created by Kun Yang on 16/2/17.
 */
@Component
public class CapacityService {

    public static final Logger LOGGER = LoggerFactory.getLogger(CapacityService.class);

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
