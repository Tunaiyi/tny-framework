package com.tny.game.basics.item.capacity;

import com.tny.game.common.number.*;
import com.tny.game.common.utils.*;

import java.util.function.BiFunction;

/**
 * CapacityUtils
 * Created by Kun Yang on 16/3/4.
 */
public interface CapacityUtils {

	static Number countCapacity(Number baseValue, CapacityGather gather, Capacity valueCap) {
		return NumberAide.add(baseValue, gather.getBaseCapacity(valueCap, valueCap.getDefault()));
	}

	static <C extends Capable> Number getValue(C owner, BiFunction<C, Capacity, Number> valueGetter, Capacity capacity, Number defNumber) {
		Number number = valueGetter.apply(owner, capacity);
		return ObjectAide.ifNull(number, defNumber);
	}

	static <C extends Capable> Number countFinalValue(Number baseValue, C owner, BiFunction<C, Capacity, Number> valueGetter,
			Capacity... capacities) {
		long base = baseValue.longValue();
		if (capacities.length == 0) {
			return base;
		}
		Capacity baseCapacity = capacities[0];
		long alterValue = 0L;
		double pctValue = 0.0;
		double effValue = 0.0;
		for (Capacity capacity : capacities) {
			switch (capacity.getValueType()) {
				case BASE:
					base += getValue(owner, valueGetter, capacity, 0L).intValue();
					break;
				case INC:
					alterValue += getValue(owner, valueGetter, capacity, 0L).intValue();
					break;
				case INC_PCT:
					pctValue += getValue(owner, valueGetter, capacity, 0.0).floatValue();
					break;
				case INC_EFF:
					effValue += getValue(owner, valueGetter, capacity, 0.0).floatValue();
					break;
				case RED:
					alterValue -= getValue(owner, valueGetter, capacity, 0L).intValue();
					break;
				case RED_PCT:
					pctValue -= getValue(owner, valueGetter, capacity, 0.0).floatValue();
					break;
				case RED_EFF:
					effValue -= getValue(owner, valueGetter, capacity, 0.0).floatValue();
					break;
			}
		}
		switch (baseCapacity.getValueType()) {
			case BASE:
				return Math.max((base * Math.max(((10000.0 + pctValue) / 10000.0), 0.0) + alterValue), 0) /
						((10000.0 + Math.max(effValue, 0)) / 10000.0);
			case INC:
			case RED:
				return Math.max(base + alterValue, 0.0);
			case INC_PCT:
			case RED_PCT:
				return Math.max(base + pctValue, 0.0);
			case INC_EFF:
			case RED_EFF:
				return Math.max(base + effValue, 0.0);
		}
		return Math.max((base * Math.max(((10000.0 + pctValue) / 10000.0), 0.0) + alterValue), 0) / ((10000.0 + Math.max(effValue, 0)) / 10000.0);
	}

}

