package com.tny.game.suite.base.capacity;

import com.tny.game.number.NumberUtils;

import java.util.Collection;

/**
 * 能力值作用者
 * Created by Kun Yang on 16/2/15.
 */
public interface CapacityGoal extends Capacitiable {

    /**
     * @return 获取能力提供者ID
     */
    long getID();

    /**
     * @return 获取目标类型
     */
    CapacityGoalType getGoalType();

    /**
     * 是否存在指定类型
     *
     * @param capacity 指定类型
     * @return 存在返回true 否则返回false
     */
    default boolean isHasCapacity(Capacity capacity) {
        for (CapacitySupplier supplier : suppliers()) {
            if (supplier != null && supplier.isHasValue(capacity))
                return true;
        }
        return false;
    }

    /**
     * 获取指定能力值提供器类型的基础能力值
     *
     * @param type     能力值提供器类型
     * @param capacity 指定能力值
     * @return 返回相关能力值
     */
    default Number getBaseCapacity(CapacitySupplyType type, Capacity capacity) {
        Number total = null;
        for (CapacitySupplier supplier : suppliers()) {
            if (supplier != null && (type == null || supplier.getSupplyType() == type)) {
                if (total == null)
                    total = supplier.getValue(capacity);
                else
                    total = NumberUtils.add(supplier.getValue(capacity, 0), total);
            }
        }
        return total == null ? capacity.getDefault() : total;
    }

    /**
     * 获取指定能力值提供器类型的基础能力值
     *
     * @param type     能力值提供器类型
     * @param capacity 指定能力值
     * @return 返回相关 byte 能力值
     */
    default byte getByteBaseCapacity(CapacitySupplyType type, Capacity capacity) {
        return getBaseCapacity(type, capacity).byteValue();
    }

    /**
     * 获取指定能力值提供器类型的基础能力值
     *
     * @param type     能力值提供器类型
     * @param capacity 指定能力值
     * @return 返回相关 short 能力值
     */
    default short getShortBaseCapacity(CapacitySupplyType type, Capacity capacity) {
        return getBaseCapacity(type, capacity).shortValue();
    }


    /**
     * 获取指定能力值提供器类型的基础能力值
     *
     * @param type     能力值提供器类型
     * @param capacity 指定能力值
     * @return 返回相关 int 能力值
     */
    default int getIntBaseCapacity(CapacitySupplyType type, Capacity capacity) {
        return getBaseCapacity(type, capacity).intValue();
    }

    /**
     * 获取指定能力值提供器类型的基础能力值
     *
     * @param type     能力值提供器类型
     * @param capacity 指定能力值
     * @return 返回相关 long 能力值
     */
    default long getLongBaseCapacity(CapacitySupplyType type, Capacity capacity) {
        return getBaseCapacity(type, capacity).longValue();
    }

    /**
     * 获取指定能力值提供器类型的基础能力值
     *
     * @param type     能力值提供器类型
     * @param capacity 指定能力值
     * @return 返回相关 float 能力值
     */
    default float getFloatBaseCapacity(CapacitySupplyType type, Capacity capacity) {
        return getBaseCapacity(type, capacity).floatValue();
    }

    /**
     * 获取指定能力值提供器类型的基础能力值
     *
     * @param type     能力值提供器类型
     * @param capacity 指定能力值
     * @return 返回相关 double 能力值
     */
    default double getDoubleBaseCapacity(CapacitySupplyType type, Capacity capacity) {
        return getBaseCapacity(type, capacity).doubleValue();
    }

    /**
     * 获取指定能力值提供器类型的基础能力值
     *
     * @param type     能力值提供器类型
     * @param capacity 指定能力值
     * @return 返回相关 Boolean 能力值
     */
    default boolean getBooleanBaseCapacity(CapacitySupplyType type, Capacity capacity) {
        return getIntBaseCapacity(type, capacity) > 0;
    }

    /**
     * 获取指定最终能力值
     *
     * @param capacity 能力值类型
     * @return 返回能力值
     */
    default Number getBaseCapacity(Capacity capacity) {
        Number total = null;
        for (CapacitySupplier supplier : suppliers()) {
            if (total == null)
                total = supplier.getValue(capacity);
            else
                total = NumberUtils.add(supplier.getValue(capacity), total);
        }
        return total;
    }

    /**
     * 获取指定最终能力值
     *
     * @param capacity   能力值类型
     * @param defaultNum 默认值
     * @return 返回能力值
     */
    default Number getBaseCapacity(Capacity capacity, Number defaultNum) {
        Number total = null;
        for (CapacitySupplier supplier : suppliers()) {
            if (total == null)
                total = supplier.getValue(capacity);
            else
                total = NumberUtils.add(supplier.getValue(capacity, capacity.getDefault()), total);
        }
        return total == null ? defaultNum : total;
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
     * @return 获取提供器列表
     */
    Collection<? extends CapacitySupplier> suppliers();

}
