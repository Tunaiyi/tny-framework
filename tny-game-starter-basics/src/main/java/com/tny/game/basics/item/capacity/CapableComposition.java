package com.tny.game.basics.item.capacity;

import com.tny.game.common.number.*;

import java.util.*;
import java.util.stream.*;

public interface CapableComposition extends Capable {

    /**
     * 获取指定能力值提供器类型的能力值
     *
     * @param type     能力值提供器类型
     * @param capacity 指定能力值
     * @return 返回相关 byte 能力值
     */
    default byte getByteCapacity(CapacitySupplierType type, Capacity capacity) {
        return getCapacity(type, capacity).byteValue();
    }

    /**
     * 获取指定能力值提供器类型的能力值
     *
     * @param type     能力值提供器类型
     * @param capacity 指定能力值
     * @return 返回相关 short 能力值
     */
    default short getShortCapacity(CapacitySupplierType type, Capacity capacity) {
        return getCapacity(type, capacity).shortValue();
    }

    /**
     * 获取指定能力值提供器类型的能力值
     *
     * @param type     能力值提供器类型
     * @param capacity 指定能力值
     * @return 返回相关 int 能力值
     */
    default int getIntCapacity(CapacitySupplierType type, Capacity capacity) {
        return getCapacity(type, capacity).intValue();
    }

    /**
     * 获取指定能力值提供器类型的能力值
     *
     * @param type     能力值提供器类型
     * @param capacity 指定能力值
     * @return 返回相关 long 能力值
     */
    default long getLongCapacity(CapacitySupplierType type, Capacity capacity) {
        return getCapacity(type, capacity).longValue();
    }

    /**
     * 获取指定能力值提供器类型的能力值
     *
     * @param type     能力值提供器类型
     * @param capacity 指定能力值
     * @return 返回相关 float 能力值
     */
    default float getFloatCapacity(CapacitySupplierType type, Capacity capacity) {
        return getCapacity(type, capacity).floatValue();
    }

    /**
     * 获取指定能力值提供器类型的能力值
     *
     * @param type     能力值提供器类型
     * @param capacity 指定能力值
     * @return 返回相关 double 能力值
     */
    default double getDoubleCapacity(CapacitySupplierType type, Capacity capacity) {
        return getCapacity(type, capacity).doubleValue();
    }

    /**
     * 获取指定能力值提供器类型的能力值
     *
     * @param type     能力值提供器类型
     * @param capacity 指定能力值
     * @return 返回相关 Boolean 能力值
     */
    default boolean getBooleanCapacity(CapacitySupplierType type, Capacity capacity) {
        return getIntCapacity(type, capacity) > 0;
    }

    /**
     * 获取指定能力值提供器类型的能力值
     *
     * @param type     能力值提供器类型
     * @param capacity 指定能力值
     * @return 返回相关能力值
     */
    default Number getCapacity(CapacitySupplierType type, Capacity capacity) {
        return suppliersStream()
                .filter(s -> s.getSupplierType() == type)
                .map(s -> s.getCapacity(capacity))
                .filter(Objects::nonNull)
                .reduce(NumberAide::add)
                .orElse(null);
    }

    /**
     * 获取指定能力值
     *
     * @param capacity   能力值类型
     * @param defaultNum 默认值
     * @return 返回能力值
     */
    @Override
    default Number getCapacity(Capacity capacity, Number defaultNum) {
        return suppliersStream()
                .map(s -> s.getCapacity(capacity))
                .filter(Objects::nonNull)
                .reduce(NumberAide::add)
                .orElse(defaultNum);
    }

    /**
     * @return 获取所有提供器的能力值组
     */
    @Override
    default Set<CapacityGroup> getAllCapacityGroups() {
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
