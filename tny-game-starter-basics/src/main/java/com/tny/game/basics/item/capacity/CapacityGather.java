package com.tny.game.basics.item.capacity;

import com.tny.game.common.number.*;

import java.util.*;
import java.util.stream.*;

/**
 * 能力值提供器集合
 * <p>
 * Created by Kun Yang on 2017/7/18.
 */
public interface CapacityGather extends Capable {

	/**
	 * 是否存在指定类型
	 *
	 * @param capacity 指定类型
	 * @return 存在返回true 否则返回false
	 */
	default boolean isHasCapacity(Capacity capacity) {
		return suppliersStream().anyMatch(s -> s.isHasValue(capacity));
	}

	/**
	 * 获取指定能力值提供器类型的基础能力值
	 *
	 * @param type     能力值提供器类型
	 * @param capacity 指定能力值
	 * @return 返回相关能力值
	 */
	default Number getBaseCapacity(CapacitySupplierType type, Capacity capacity) {
		return suppliersStream()
				.filter(s -> s.getSupplierType() == type)
				.map(s -> s.getValue(capacity))
				.filter(Objects::nonNull)
				.reduce(NumberAide::add)
				.orElse(null);
	}

	/**
	 * 获取指定能力值提供器类型的基础能力值
	 *
	 * @param type     能力值提供器类型
	 * @param capacity 指定能力值
	 * @return 返回相关 byte 能力值
	 */
	default byte getByteBaseCapacity(CapacitySupplierType type, Capacity capacity) {
		return getBaseCapacity(type, capacity).byteValue();
	}

	/**
	 * 获取指定能力值提供器类型的基础能力值
	 *
	 * @param type     能力值提供器类型
	 * @param capacity 指定能力值
	 * @return 返回相关 short 能力值
	 */
	default short getShortBaseCapacity(CapacitySupplierType type, Capacity capacity) {
		return getBaseCapacity(type, capacity).shortValue();
	}

	/**
	 * 获取指定能力值提供器类型的基础能力值
	 *
	 * @param type     能力值提供器类型
	 * @param capacity 指定能力值
	 * @return 返回相关 int 能力值
	 */
	default int getIntBaseCapacity(CapacitySupplierType type, Capacity capacity) {
		return getBaseCapacity(type, capacity).intValue();
	}

	/**
	 * 获取指定能力值提供器类型的基础能力值
	 *
	 * @param type     能力值提供器类型
	 * @param capacity 指定能力值
	 * @return 返回相关 long 能力值
	 */
	default long getLongBaseCapacity(CapacitySupplierType type, Capacity capacity) {
		return getBaseCapacity(type, capacity).longValue();
	}

	/**
	 * 获取指定能力值提供器类型的基础能力值
	 *
	 * @param type     能力值提供器类型
	 * @param capacity 指定能力值
	 * @return 返回相关 float 能力值
	 */
	default float getFloatBaseCapacity(CapacitySupplierType type, Capacity capacity) {
		return getBaseCapacity(type, capacity).floatValue();
	}

	/**
	 * 获取指定能力值提供器类型的基础能力值
	 *
	 * @param type     能力值提供器类型
	 * @param capacity 指定能力值
	 * @return 返回相关 double 能力值
	 */
	default double getDoubleBaseCapacity(CapacitySupplierType type, Capacity capacity) {
		return getBaseCapacity(type, capacity).doubleValue();
	}

	/**
	 * 获取指定能力值提供器类型的基础能力值
	 *
	 * @param type     能力值提供器类型
	 * @param capacity 指定能力值
	 * @return 返回相关 Boolean 能力值
	 */
	default boolean getBooleanBaseCapacity(CapacitySupplierType type, Capacity capacity) {
		return getIntBaseCapacity(type, capacity) > 0;
	}

	/**
	 * 获取指定最终能力值
	 *
	 * @param capacity 能力值类型
	 * @return 返回能力值
	 */
	default Number getBaseCapacity(Capacity capacity) {
		return getBaseCapacity(capacity, null);
	}

	/**
	 * 获取指定最终能力值
	 *
	 * @param capacity   能力值类型
	 * @param defaultNum 默认值
	 * @return 返回能力值
	 */
	default Number getBaseCapacity(Capacity capacity, Number defaultNum) {
		return suppliersStream()
				.map(s -> s.getValue(capacity))
				.filter(Objects::nonNull)
				.reduce(NumberAide::add)
				.orElse(defaultNum);
	}

	/**
	 * 获取指定最终能力值
	 *
	 * @param capacity 能力值类型
	 * @return 返回能力值
	 */
	default Number getFinalCapacity(Capacity capacity) {
		return capacity.countFinalCapacity(this);
	}

	/**
	 * 获取指定最终能力值
	 *
	 * @param base     基础值
	 * @param capacity 能力值类型
	 * @return 返回能力值
	 */
	default Number getFinalCapacity(Number base, Capacity capacity) {
		return capacity.countFinalCapacity(base, this);
	}

	/**
	 * 获取指定基础能力值
	 *
	 * @param capacity 指定能力值
	 * @return 返回相关 byte 能力值
	 */
	default byte getByteBaseCapacity(Capacity capacity) {
		return getBaseCapacity(capacity, capacity.getDefault()).byteValue();
	}

	/**
	 * 获取指定最终能力值
	 *
	 * @param capacity 指定能力值
	 * @return 返回相关 byte 能力值
	 */
	default byte getByteFinalCapacity(Capacity capacity) {
		return getFinalCapacity(capacity).byteValue();
	}

	/**
	 * 获取指定最终能力值
	 *
	 * @param capacity 指定能力值
	 * @return 返回相关 byte 能力值
	 */
	default byte getByteFinalCapacity(Number base, Capacity capacity) {
		return getFinalCapacity(base, capacity).byteValue();
	}

	/**
	 * 获取指定基础能力值
	 *
	 * @param capacity 指定能力值
	 * @return 返回相关 short 能力值
	 */
	default short getShortBaseCapacity(Capacity capacity) {
		return getBaseCapacity(capacity, capacity.getDefault()).shortValue();
	}

	/**
	 * 获取指定最终能力值
	 *
	 * @param capacity 指定能力值
	 * @return 返回相关 short 能力值
	 */
	default short getShortFinalCapacity(Capacity capacity) {
		return getFinalCapacity(capacity).shortValue();
	}

	/**
	 * 获取指定最终能力值
	 *
	 * @param capacity 指定能力值
	 * @return 返回相关 short 能力值
	 */
	default short getShortFinalCapacity(Number base, Capacity capacity) {
		return getFinalCapacity(base, capacity).shortValue();
	}

	/**
	 * 获取指定基础能力值
	 *
	 * @param capacity 能力值类型
	 * @return 返回 Int 能力值
	 */
	default int getIntBaseCapacity(Capacity capacity) {
		return getBaseCapacity(capacity, capacity.getDefault()).intValue();
	}

	/**
	 * 获取指定最终能力值
	 *
	 * @param capacity 指定能力值
	 * @return 返回相关 int 能力值
	 */
	default int getIntFinalCapacity(Capacity capacity) {
		return getFinalCapacity(capacity).intValue();
	}

	/**
	 * 获取指定最终能力值
	 *
	 * @param capacity 指定能力值
	 * @return 返回相关 short 能力值
	 */
	default int getIntFinalCapacity(Number base, Capacity capacity) {
		return getFinalCapacity(base, capacity).intValue();
	}

	/**
	 * 获取指定基础能力值
	 *
	 * @param capacity 能力值类型
	 * @return 返回 long 能力值
	 */
	default long getLongBaseCapacity(Capacity capacity) {
		return getBaseCapacity(capacity, capacity.getDefault()).longValue();
	}

	/**
	 * 获取指定最终能力值
	 *
	 * @param capacity 指定能力值
	 * @return 返回相关 long 能力值
	 */
	default long getLongFinalCapacity(Capacity capacity) {
		return getFinalCapacity(capacity).longValue();
	}

	/**
	 * 获取指定最终能力值
	 *
	 * @param capacity 指定能力值
	 * @return 返回相关 long 能力值
	 */
	default long getLongFinalCapacity(Number base, Capacity capacity) {
		return getFinalCapacity(base, capacity).longValue();
	}

	/**
	 * 获取指定基础能力值
	 *
	 * @param capacity 能力值类型
	 * @return 返回 float 能力值
	 */
	default float getFloatBaseCapacity(Capacity capacity) {
		return getBaseCapacity(capacity, capacity.getDefault()).floatValue();
	}

	/**
	 * 获取指定最终能力值
	 *
	 * @param capacity 指定能力值
	 * @return 返回相关 float 能力值
	 */
	default float getFloatFinalCapacity(Capacity capacity) {
		return getFinalCapacity(capacity).floatValue();
	}

	/**
	 * 获取指定最终能力值
	 *
	 * @param capacity 指定能力值
	 * @return 返回相关 float 能力值
	 */
	default float getFloatFinalCapacity(Number base, Capacity capacity) {
		return getFinalCapacity(base, capacity).floatValue();
	}

	/**
	 * 获取指定基础能力值
	 *
	 * @param capacity 能力值类型
	 * @return 返回 double 能力值
	 */
	default double getDoubleBaseCapacity(Capacity capacity) {
		return getBaseCapacity(capacity, capacity.getDefault()).doubleValue();
	}

	/**
	 * 获取指定最终能力值
	 *
	 * @param capacity 指定能力值
	 * @return 返回相关 double 能力值
	 */
	default double getDoubleFinalCapacity(Capacity capacity) {
		return getFinalCapacity(capacity).doubleValue();
	}

	/**
	 * 获取指定最终能力值
	 *
	 * @param capacity 指定能力值
	 * @return 返回相关 double 能力值
	 */
	default double getDoubleFinalCapacity(Number base, Capacity capacity) {
		return getFinalCapacity(base, capacity).doubleValue();
	}

	/**
	 * 获取指定基础能力值
	 *
	 * @param capacity 能力值类型
	 * @return 返回 boolean 能力值
	 */
	default boolean getBooleanBaseCapacity(Capacity capacity) {
		return getIntBaseCapacity(capacity) > 0;
	}

	/**
	 * 获取指定最终能力值
	 *
	 * @param capacity 指定能力值
	 * @return 返回相关 boolean 能力值
	 */
	default boolean getBooleanFinalCapacity(Capacity capacity) {
		return getFinalCapacity(capacity).intValue() > 0;
	}

	/**
	 * 获取指定最终能力值
	 *
	 * @param capacity 指定能力值
	 * @return 返回相关 boolean 能力值
	 */
	default boolean getBooleanFinalCapacity(Number base, Capacity capacity) {
		return getFinalCapacity(base, capacity).intValue() > 0;
	}

	/**
	 * 收集Goal中指定capacities的能力值
	 *
	 * @param collector  能力值收集器
	 * @param capacities 能力值类型
	 * @return 返回类型, 存在的Capacity为null
	 */
	default void collectCapacities(CapacityCollector collector, Capacity... capacities) {
		collectCapacities(collector, Arrays.asList(capacities));
	}

	/**
	 * 收集Goal中指定capacities的能力值
	 *
	 * @param collector  能力值收集器
	 * @param capacities 能力值类型
	 * @return 返回类型, 存在的Capacity为null
	 */
	default void collectCapacities(CapacityCollector collector, Collection<? extends Capacity> capacities) {
		suppliersStream().forEach(g -> g.collectValues(collector, capacities));
	}

	/**
	 * @return 获取所有提供器的能力值组
	 */
	default Set<CapacityGroup> getSuppliersCapacityGroups() {
		return suppliersStream()
				.flatMap(s -> s.getAllCapacityGroups().stream())
				.collect(Collectors.toSet());
	}

	/**
	 * @return 获取提供器列表
	 */
	Collection<? extends CapacitySupplier> suppliers();

	/**
	 * @return 提供器列Stream
	 */
	default Stream<? extends CapacitySupplier> suppliersStream() {
		return suppliers().stream();
	}

}
