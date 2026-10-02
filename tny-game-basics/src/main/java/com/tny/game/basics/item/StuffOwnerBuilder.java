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

import java.util.*;

/**
 * Storage构建器
 *
 * @param <S>
 * @param <O>
 * @param <B>
 * @author KGTny
 */
@SuppressWarnings("unchecked")
public abstract class StuffOwnerBuilder<
        IM extends ItemModel,
        S extends Stuff<?>,
        O extends BaseStuffOwner<IM, ?, S>,
        B extends StuffOwnerBuilder<IM, S, O, B>>
        extends ItemBuilder<O, IM, B> {

    /**
     * 物品模型列表
     */
    private Set<S> itemSet = new HashSet<>();

    /**
     * 添加item对象
     *
     * @param item 添加的item对象
     * @return 返回storageBuilder对象
     */
    public B addItem(S item) {
        this.itemSet.add(item);
        return (B) this;
    }

    /**
     * 添加item对象列表
     *
     * @param itemList 添加的item对象列表
     * @return 返回storageBuilder对象
     */
    public B addItem(Set<S> itemList) {
        for (S item : itemList)
            this.itemSet.add(item);
        return (B) this;
    }

    /**
     * 构建storage对象
     *
     * @return Storage对象
     */
    public O createItem() {
        O entity = createOwner();
        entity.setStuffs(this.itemSet);
        return entity;
    }

    /**
     * 获取构建storage
     *
     * @return
     */
    protected abstract O createOwner();

}
