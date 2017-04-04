package com.tny.game.suite.base.capacity;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 内部能力值作用对象
 * Created by Kun Yang on 16/2/15.
 */
public abstract class AbstractCapacityGoal extends InnerCapacityGoal {

    protected CapacityGoalType goalType;

    protected volatile Set<CapacitySupplier> suppliers = new CopyOnWriteArraySet<>();

    protected AbstractCapacityGoal(CapacityGoalType goalType) {
        this.goalType = goalType;
    }

    @Override
    public CapacityGoalType getGoalType() {
        return goalType;
    }

    @Override
    public Collection<CapacitySupplier> suppliers() {
        return Collections.unmodifiableCollection(suppliers);
    }

    @Override
    public void clear() {
        this.suppliers = new CopyOnWriteArraySet<>();
    }
}
