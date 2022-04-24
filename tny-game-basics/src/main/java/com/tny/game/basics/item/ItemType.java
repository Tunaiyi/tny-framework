package com.tny.game.basics.item;

import com.tny.game.common.enums.*;

/**
 * 事物类型接口
 *
 * @author KGTny
 */
public interface ItemType extends IntEnumerable {

    int ID_TAIL_SIZE = 1000000;

    /**
     * 获取别名头
     */
    String getAliasHead();

    /**
     * @return 描述 秒速
     */
    String getDesc();

    /**
     * @return id前缀
     */
    default int getIdHead() {
        return getId() / ID_TAIL_SIZE;
    }

    /**
     * 创建 itemId
     *
     * @param index 索引
     * @return 返回 itemID
     */
    default long itemIdOf(int index) {
        return Long.parseLong(this.getIdHead() + "" + index);
    }

    /**
     * 创建 item别名
     *
     * @param alisa 别名
     * @return 返回创建别名
     */
    default String alisaOf(String alisa) {
        return getAliasHead() + "$" + alisa;
    }

}