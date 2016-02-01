package com.tny.game.base.item;

import com.tny.game.common.enums.EnumID;

/**
 * 事物类型接口
 *
 * @author KGTny
 */
public interface ItemType extends EnumID<Integer> {

    /**
     * 获取别名头
     *
     * @return
     */
    String getAliasHead();

    /**
     * 是否有实体
     *
     * @return
     */
    boolean hasEntity();

    String getDesc();

    Class<?> getItemManagerClass();

    Class<?> getOwnerManagerClass();

    Class<?> getItemModelManagerClass();
}