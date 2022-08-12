/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * @author KGTny
 * @ClassName: owner
 * @Description: 物品项存储器
 * @date 2011-11-3 上午9:50:52
 * <p>
 * <p>
 * <br>
 */
public interface StuffOwner<M extends ItemModel, S extends Stuff<?>> extends Item<M> {

    @Override
    default long getId() {
        return this.getItemType().getId();
    }

    /**
     * 获取某事物的信息 <br>
     *
     * @param id 物品ID
     * @return 物品信息
     */
    S getItemById(long id);

    /**
     * 获取某事物的信息 <br>
     *
     * @param modelId 物品ID
     * @return 物品信息
     */
    default List<S> getItemsByModelId(int modelId) {
        S stuff = getItemByModelId(modelId);
        if (stuff != null) {
            return ImmutableList.of(stuff);
        }
        return ImmutableList.of();
    }

    /**
     * 获取某事物的信息 <br>
     *
     * @param modelId 物品ID
     * @return 物品信息
     */
    S getItemByModelId(int modelId);

}