package com.tny.game.base.item;

import com.tny.game.common.enums.*;

/**
 * 事物类型接口
 *
 * @author KGTny
 */
public interface ItemType extends EnumIdentifiable<Integer> {

    int ID_TAIL_SIZE = 1000000;


    /**
     * 获取别名头
     *
     * @return
     */
    String getAliasHead();

    String getDesc();

    default int getIdHead() {
        return getId() / ID_TAIL_SIZE;
    }

    // Class<?> getItemManagerClass();

    // Class<?> getOwnerManagerClass();

    // Class<?> getItemModelManagerClass();
}