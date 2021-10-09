package com.tny.game.basics.item.capacity;

/**
 * 可通过能力接口
 * Created by Kun Yang on 16/3/12.
 */
public interface InnerCapacitySupply extends CapacitySupply {

    /**
     * 刷新
     *
     * @param supplier 持有的提供器
     */
    void refresh(CapacitySupplier supplier);

    /**
     * 失效
     *
     * @param supplier 持有的提供器
     */
    void invalid(CapacitySupplier supplier);

    /**
     * 生效
     *
     * @param supplier 持有的提供器
     */
    void effect(CapacitySupplier supplier);

}
