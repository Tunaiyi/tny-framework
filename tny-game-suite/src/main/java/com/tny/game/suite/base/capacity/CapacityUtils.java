package com.tny.game.suite.base.capacity;

import com.tny.game.common.number.NumberAide;
import com.tny.game.common.utils.ObjectAide;

import java.util.function.*;

/**
 * CapacityUtils
 * Created by Kun Yang on 16/3/4.
 */
public interface CapacityUtils {

    static Number countCapacity(Number baseValue, CapacityGather gather, Capacity valueCap) {
        return NumberAide.add(baseValue, gather.getBaseCapacity(valueCap, valueCap.getDefault()));
    }

    static <C extends Capacitiable> Number getValue(C owner, BiFunction<C, Capacity, Number> valueGetter, Capacity capacity, Number defNumber) {
        Number number = valueGetter.apply(owner, capacity);
        return ObjectAide.ifNull(number, defNumber);
    }

    static <C extends Capacitiable> Number countFinalValue(Number baseValue, C owner, BiFunction<C, Capacity, Number> valueGetter, Capacity... capacities) {
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
                    base += getValue(owner, valueGetter, capacity, 0).intValue();
                    break;
                case INC:
                    alterValue += getValue(owner, valueGetter, capacity, 0).intValue();
                    break;
                case INC_PCT:
                    pctValue += getValue(owner, valueGetter, capacity, 0.F).floatValue();
                    break;
                case INC_EFF:
                    effValue += getValue(owner, valueGetter, capacity, 0.F).floatValue();
                    break;
                case RED:
                    alterValue -= getValue(owner, valueGetter, capacity, 0).intValue();
                    break;
                case RED_PCT:
                    pctValue -= getValue(owner, valueGetter, capacity, 0.F).floatValue();
                    break;
                case RED_EFF:
                    effValue -= getValue(owner, valueGetter, capacity, 0.F).floatValue();
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

