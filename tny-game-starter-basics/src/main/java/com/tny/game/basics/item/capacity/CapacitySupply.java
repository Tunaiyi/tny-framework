package com.tny.game.basics.item.capacity;

import java.util.*;

/**
 * 可通过能力接口
 * Created by Kun Yang on 16/3/12.
 */
public interface CapacitySupply extends Capable {

	/**
	 * 是否存在 capacity 能力值
	 *
	 * @param capacity 能力类型
	 * @return 存在 capacity 能力值返回 true, 否则返回false
	 */
	boolean isHasValue(Capacity capacity);

	/**
	 * @param capacity 能力类型
	 * @return 获取  capacity 能力值
	 */
	Number getValue(Capacity capacity);

	/**
	 * @param capacity   能力类型
	 * @param defaultNum 默认值
	 * @return 获取  capacity 能力值
	 */
	Number getValue(Capacity capacity, Number defaultNum);

	/**
	 * @param capacity 能力类型
	 * @return 获取  capacity short 能力值
	 */
	default short getShortVaule(Capacity capacity) {
		return getValue(capacity, capacity.getDefault()).shortValue();
	}

	/**
	 * @param capacity 能力类型
	 * @return 获取  capacity int 能力值
	 */
	default int getIntVaule(Capacity capacity) {
		return getValue(capacity, capacity.getDefault()).intValue();
	}

	/**
	 * @param capacity 能力类型
	 * @return 获取  capacity long 能力值
	 */
	default long getLongVaule(Capacity capacity) {
		return getValue(capacity, capacity.getDefault()).longValue();
	}

	/**
	 * @param capacity 能力类型
	 * @return 获取  capacity boolean 能力值
	 */
	default boolean getBooleanValue(Capacity capacity) {
		return getValue(capacity, capacity.getDefault()).intValue() > 0;
	}

	/**
	 * @param capacity 能力类型
	 * @return 获取  capacity double 能力值
	 */
	default double getDoubleValue(Capacity capacity) {
		return getValue(capacity, capacity.getDefault()).doubleValue();
	}

	/**
	 * @param capacity 能力类型
	 * @return 获取  capacity float 能力值
	 */
	default float getFloatValue(Capacity capacity) {
		return getValue(capacity, capacity.getDefault()).shortValue();
	}

	/**
	 * 收集Supplier中指定capacities的能力值
	 *
	 * @param collector  收集器
	 * @param capacities 能力值类型
	 */
	default void collectValues(CapacityCollector collector, Capacity... capacities) {
		collectValues(collector, Arrays.asList(capacities));
	}

	/**
	 * 收集Supplier中指定capacities的能力值
	 *
	 * @param collector  收集器
	 * @param capacities 能力值类型
	 */
	default void collectValues(CapacityCollector collector, Collection<? extends Capacity> capacities) {
		for (Capacity capacity : capacities)
			collector.collect(capacity, getValue(capacity));
	}

	/**
	 * @return 获取所有能力值组
	 */
	Set<CapacityGroup> getAllCapacityGroups();

	/**
	 * @return 获取所有相关的所有 能力值
	 */
	Map<Capacity, Number> getAllValues();

}
