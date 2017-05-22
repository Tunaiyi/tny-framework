package com.tny.game.suite.base.capacity;

import org.joda.time.DateTime;

import java.util.Map;
import java.util.Set;

/**
 * 游戏能力值提供器
 * Created by Kun Yang on 16/2/15.
 */
public interface TimeoutCapacitySupplier extends CapacitySupplier {

    class WrapTimeCheckerSupplier implements CapacitySupplier {

        private TimeChecker timeChecker;

        private TimeoutCapacitySupplier supplier;

        private WrapTimeCheckerSupplier(TimeoutCapacitySupplier supplier, TimeChecker timeChecker) {
            this.supplier = supplier;
            this.timeChecker = timeChecker;
        }

        @Override
        public long getID() {
            return supplier.getID();
        }

        @Override
        public int getItemID() {
            return supplier.getItemID();
        }

        @Override
        public long getPlayerID() {
            return supplier.getPlayerID();
        }

        @Override
        public CapacitySupplyType getSupplyType() {
            return supplier.getSupplyType();
        }

        @Override
        public boolean isHasValue(Capacity capacity) {
            return supplier.isHasValue(capacity, timeChecker.getCheckAt());
        }

        @Override
        public Number getValue(Capacity capacity) {
            return supplier.getValue(capacity, timeChecker.getCheckAt());
        }

        @Override
        public Number getValue(Capacity capacity, Number defaultNum) {
            return supplier.getValue(capacity, timeChecker.getCheckAt(), defaultNum);
        }

        @Override
        public Map<Capacity, Number> getAllCapacityValue() {
            return supplier.getAllCapacityValue(timeChecker.getCheckAt());
        }

        // @Override
        // public Set<Capacity> getSupplyCapacities() {
        //     return supplier.getSupplyCapacities(timeChecker.getCheckAt());
        // }
    }

    /**
     * @return 包装
     */
    default CapacitySupplier wrap(TimeChecker checker) {
        return new WrapTimeCheckerSupplier(this, checker);
    }

    /**
     * 剩余检测时间
     *
     * @param time 当前检测时间
     * @return 返回剩余检测时间, 若<0表示一直有效
     */
    default long countRemainTime(long time) {
        DateTime endAt = getEndAt();
        if (endAt == null)
            return -1;
        return Math.max(endAt.getMillis() - time, 0);
    }

    /**
     * @return 获取结束检测时间
     */
    DateTime getEndAt();

    /**
     * 检测时间点是否有效
     *
     * @param time 检测时间
     * @return 返回是否有效
     */
    default boolean isWork(long time) {
        if (time <= 0)
            return false;
        long remainTime = countRemainTime(time);
        return remainTime < 0 || remainTime > 0;
    }

    /**
     * @return 当前时间是否愚孝
     */
    default boolean isWorking() {
        return isWork(System.currentTimeMillis());
    }


    /**
     * 是否存在 capacity 能力值
     *
     * @param capacity 能力类型
     * @param time     检测时间
     * @return 存在 capacity 能力值返回 true, 否则返回false
     */
    boolean isHasValue(Capacity capacity, long time);


    @Override
    default boolean isHasValue(Capacity capacity) {
        return isHasValue(capacity, System.currentTimeMillis());
    }

    /**
     * @param capacity 能力类型
     * @param time     检测时间
     * @return 获取  capacity 能力值
     */
    Number getValue(Capacity capacity, long time);

    /**
     * @param capacity 能力类型
     * @param time     检测时间
     * @return 获取  capacity 能力值
     */
    Number getValue(Capacity capacity, long time, Number defaultValue);

    @Override
    default Number getValue(Capacity capacity, Number defaultValue) {
        return this.getValue(capacity, System.currentTimeMillis(), defaultValue);
    }

    @Override
    default Number getValue(Capacity capacity) {
        return this.getValue(capacity, System.currentTimeMillis());
    }

    /**
     * @param capacity 能力类型
     * @param time     检测时间
     * @return 获取  capacity short 能力值
     */
    default short getShortValue(Capacity capacity, long time) {
        return getValue(capacity, time).shortValue();
    }

    /**
     * @param capacity 能力类型
     * @param time     检测时间
     * @return 获取  capacity int 能力值
     */
    default int getIntValue(Capacity capacity, long time) {
        return getValue(capacity, time).intValue();
    }

    /**
     * @param capacity 能力类型
     * @param time     检测时间
     * @return 获取  capacity long 能力值
     */
    default long getLongValue(Capacity capacity, long time) {
        return getValue(capacity, time).longValue();
    }

    /**
     * @param capacity 能力类型
     * @param time     检测时间
     * @return 获取  capacity boolean 能力值
     */
    default boolean getBooleanValue(Capacity capacity, long time) {
        return getValue(capacity, time).intValue() > 0;
    }

    /**
     * @param capacity 能力类型
     * @param time     检测时间
     * @return 获取  capacity double 能力值
     */
    default double getDoubleValue(Capacity capacity, long time) {
        return getValue(capacity, time).doubleValue();
    }

    /**
     * @param capacity 能力类型
     * @param time     检测时间
     * @return 获取  capacity float 能力值
     */
    default float getFloatValue(Capacity capacity, long time) {
        return getValue(capacity, time).shortValue();
    }

    /**
     * * @param time     检测时间
     *
     * @return 获取所有相关的所有 能力值
     */
    Map<Capacity, Number> getAllCapacityValue(long time);

    @Override
    default Map<Capacity, Number> getAllCapacityValue() {
        return getAllCapacityValue(System.currentTimeMillis());
    }

    /**
     * @param time 检测时间
     * @return 获取所有Capacity类型
     */
    Set<Capacity> getSupplyCapacities(long time);

    // @Override
    // default Set<Capacity> getSupplyCapacities() {
    //     return getSupplyCapacities(System.currentTimeMillis());
    // }

}