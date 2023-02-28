/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
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
     * 创建 itemId
     *
     * @param index 索引
     * @return 返回 itemID
     */
    default long itemIdOf(long index) {
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