package com.tny.game.basics.item.capacity;

/**
 * 能力者-从能力提供器获取能力
 * <p>
 * Created by Kun Yang on 2017/7/18.
 */
public interface CapacitySettler extends CapableComposition {

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
     * 获取指定能力值
     *
     * @param capacity 指定能力值
     * @return 返回相关 byte 能力值
     */
    default byte getByteCapacity(Capacity capacity) {
        return getCapacity(capacity, capacity.getDefault()).byteValue();
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

}
