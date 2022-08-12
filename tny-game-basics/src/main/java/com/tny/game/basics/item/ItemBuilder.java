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

import com.tny.game.common.utils.*;

/**
 * item构建器
 *
 * @param <I>
 * @param <IM>
 * @param <B>
 * @author KGTny
 */
@SuppressWarnings("unchecked")
public abstract class ItemBuilder<I extends BaseItem<IM>, IM extends ItemModel, B extends ItemBuilder<I, IM, B>> {

    /**
     * 玩家ID
     */
    protected long playerId;

    /**
     * 物品模型
     */
    protected IM itemModel;

    /**
     * 设置玩家ID <br>
     *
     * @param playerId 玩家id
     * @return 构建器
     */
    public B setPlayerId(long playerId) {
        this.playerId = playerId;
        return (B)this;
    }

    /**
     * 设置事物模型 <br>
     *
     * @param model 物品模型
     * @return 构建器
     */
    public B setModel(IM model) {
        Asserts.checkNotNull(model);
        this.itemModel = model;
        return (B)this;
    }

    /**
     * 构建事物对象
     *
     * @return
     */
    public I build() {
        I entity = createItem();
        entity.setPlayerId(this.playerId);
        entity.setModel(this.itemModel);
        afterModelItem(entity);
        return entity;
    }

    /**
     * 构建事物对象
     *
     * @return
     */
    protected void afterModelItem(I item) {

    }

    /**
     * 构建事物对象
     *
     * @return
     */
    protected abstract I createItem();

}
