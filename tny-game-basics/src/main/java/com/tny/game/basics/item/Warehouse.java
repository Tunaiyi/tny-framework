package com.tny.game.basics.item;

import java.util.function.BiFunction;

/**
 * 残酷系统
 *
 * @author KGTny
 */
public interface Warehouse extends Any {

    /**
     * 获取指定itemType的Storage对象
     *
     * @param itemType 事物类型
     * @return 返回storage对象
     */
    <O extends StuffOwner<?, ?>> O loadOwner(ItemType itemType, BiFunction<Warehouse, ItemType, O> ownerSupplier);

}
