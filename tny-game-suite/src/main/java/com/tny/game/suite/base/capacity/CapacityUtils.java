package com.tny.game.suite.base.capacity;

import com.tny.game.number.NumberUtils;

/**
 * CapacityUtils
 * Created by Kun Yang on 16/3/4.
 */
public interface CapacityUtils {

    static Number countCapacity(Number baseValue, CapacityGoal goal, Capacity valueCap) {
        return NumberUtils.add(baseValue, goal.getBaseCapacity(valueCap, valueCap.getDefault()));
    }

    static Number countFinalValue(Number baseValue, CapacityGoal goal, Capacity... capacities) {
        int base = baseValue.intValue();
        if (capacities.length == 0)
            return base;
        Capacity baseCapacity = capacities[0];
        int alterValue = 0;
        float pctValue = 0.F;
        float effValue = 0.F;
        for (Capacity capacity : capacities) {
            switch (capacity.getValueType()) {
                case BASE:
                    base += goal.getIntBaseCapacity(capacity);
                    break;
                case INC:
                    alterValue += goal.getIntBaseCapacity(capacity);
                    break;
                case INC_PCT:
                    pctValue += goal.getFloatBaseCapacity(capacity);
                    break;
                case INC_EFF:
                    effValue += goal.getFloatBaseCapacity(capacity);
                    break;
                case RED:
                    alterValue -= goal.getIntBaseCapacity(capacity);
                    break;
                case RED_PCT:
                    pctValue -= goal.getFloatBaseCapacity(capacity);
                    break;
                case RED_EFF:
                    effValue -= goal.getFloatBaseCapacity(capacity);
                    break;
            }
        }
        switch (baseCapacity.getValueType()) {
            case BASE:
                return Math.max((base * Math.max(((10000.F + pctValue) / 10000.F), 0.F) + alterValue), 0) / ((10000.F + Math.max(effValue, 0)) / 10000.F);
            case INC:
            case RED:
                return Math.max(base + alterValue, 0.F);
            case INC_PCT:
            case RED_PCT:
                return Math.max(base + pctValue, 0.F);
            case INC_EFF:
            case RED_EFF:
                return Math.max(base + effValue, 0.F);
        }
        return Math.max((base * Math.max(((10000.F + pctValue) / 10000.F), 0.F) + alterValue), 0) / ((10000.F + Math.max(effValue, 0)) / 10000.F);
    }

}

