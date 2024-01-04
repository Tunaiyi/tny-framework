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

/**
 * stuff模型接口，于数量相关的事物模型接口
 *
 * @author KGTny
 */
public interface SingleStuffModel extends ItemModel {

    /**
     * @return 是否有数量限制 <br>
     */
    default boolean isNumberLimit() {
        return false;
    }

    /**
     * 数量上线 <br>
     *
     * @param stuff 计算参数
     * @return -1 表示无限制
     */
    default Number countNumberLimit(Stuff<?> stuff) {
        return -1;
    }

}