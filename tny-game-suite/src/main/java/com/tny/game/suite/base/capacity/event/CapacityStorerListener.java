package com.tny.game.suite.base.capacity.event;

import com.tny.game.suite.base.capacity.CapacityStorer;
import com.tny.game.suite.base.capacity.ExpireCapacityGoal;
import com.tny.game.suite.base.capacity.ExpireCapacitySupplier;

import java.util.Collection;

/**
 * 能力值存储监听器
 * Created by Kun Yang on 2017/7/19.
 */
public interface CapacityStorerListener {

    /**
     * 触发链接Supplier
     *
     * @param storer    存储器
     * @param suppliers 链接的能力值列表
     */
    default void onLinkSupplier(CapacityStorer storer, Collection<ExpireCapacitySupplier> suppliers) {
    }

    /**
     * 触发存储Supplier
     *
     * @param storer    存储器
     * @param suppliers 存储的能力值列表
     */
    default void onSaveSupplier(CapacityStorer storer, Collection<ExpireCapacitySupplier> suppliers) {
    }
    ;

    /**
     * 触发删除Supplier
     *
     * @param storer    存储器
     * @param suppliers 删除的能力值列表
     */
    default void onDeleteSupplier(CapacityStorer storer, Collection<ExpireCapacitySupplier> suppliers) {
    }
    ;

    /**
     * 触发有效时间变化
     *
     * @param storer    存储器
     * @param suppliers 改变的能力值列表
     */
    default void onExpireSupplier(CapacityStorer storer, Collection<ExpireCapacitySupplier> suppliers) {
    }
    ;

    /**
     * 触发存储Goal
     *
     * @param storer 存储器
     * @param goals  存储的目标列表
     */
    default void onSaveGoal(CapacityStorer storer, Collection<ExpireCapacityGoal> goals) {
    }
    ;

    /**
     * 触发删除Goal
     *
     * @param storer 存储器
     * @param goals  删除的目标列表
     */
    default void onDeleteGoal(CapacityStorer storer, Collection<ExpireCapacityGoal> goals) {
    }
    ;


    /**
     * 触发有效时间变化
     *
     * @param storer 存储器
     * @param goals  改变的目标列表
     */
    default void onExpireGoal(CapacityStorer storer, Collection<ExpireCapacityGoal> goals) {
    }
    ;

}
