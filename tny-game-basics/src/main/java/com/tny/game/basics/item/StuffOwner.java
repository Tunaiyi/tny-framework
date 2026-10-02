/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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