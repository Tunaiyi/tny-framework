package com.tny.game.basics.item.capacity;

import com.tny.game.common.enums.*;

import java.util.Collection;

/**
 * Created by Kun Yang on 2017/4/4.
 */
public class CapacitySupplierTypes {

    protected static EnumeratorHolder<CapacitySupplierType> holder = new EnumeratorHolder<>();

    private CapacitySupplierTypes() {
    }

    static void register(CapacitySupplierType value) {
        holder.register(value);
    }

    public static <T extends CapacitySupplierType> T of(String key) {
        return holder.check(key, "获取 {} CapacitySupplyType 不存在", key);
    }

    public static <T extends CapacitySupplierType> T of(int id) {
        return holder.check(id, "获取 ID为 {} 的 CapacitySupplyType 不存在", id);
    }

    public static Collection<CapacitySupplierType> getAll() {
        return holder.allValues();
    }

}
