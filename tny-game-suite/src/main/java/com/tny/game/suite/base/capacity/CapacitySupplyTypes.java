package com.tny.game.suite.base.capacity;

import com.tny.game.common.enums.EnumeratorHolder;

import java.util.Collection;

/**
 * Created by Kun Yang on 2017/4/4.
 */
public class CapacitySupplyTypes {

    protected static EnumeratorHolder<CapacitySupplyType> holder = new EnumeratorHolder<>();


    private CapacitySupplyTypes() {
    }

    static void register(CapacitySupplyType value) {
        holder.register(value);
    }

    public static <T extends CapacitySupplyType> T of(String key) {
        return holder.ofAndCheck(key, "获取 {} CapacitySupplyType 不存在", key);
    }

    public static <T extends CapacitySupplyType> T of(int id) {
        return holder.ofAndCheck(id, "获取 ID为 {} 的 CapacitySupplyType 不存在", id);
    }

    public static Collection<CapacitySupplyType> getAll() {
        return holder.values();
    }

}
