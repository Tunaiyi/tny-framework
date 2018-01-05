package com.tny.game.base.item;

import com.tny.game.common.enums.*;

/**
 * 事物类型接口
 *
 * @author KGTny
 */
public interface ItemType extends EnumID<Integer> {

    int ID_TAIL_SIZE = 1000000;


    /**
     * 获取别名头
     *
     * @return
     */
    String getAliasHead();

    String getDesc();

    default int getIDHead() {
        return getID() / ID_TAIL_SIZE;
    }

    // Class<?> getItemManagerClass();

    // Class<?> getOwnerManagerClass();

    // Class<?> getItemModelManagerClass();
}