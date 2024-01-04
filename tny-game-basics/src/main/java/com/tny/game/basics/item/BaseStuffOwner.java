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

import com.tny.game.basics.item.behavior.*;
import com.tny.game.common.context.*;

import java.util.Collection;

/**
 * Created by Kun Yang on 16/1/28.
 */
public abstract class BaseStuffOwner<IM extends ItemModel, SM extends StuffModel, S extends Stuff<?>>
        extends BaseItem<IM>
        implements StuffOwner<IM, S> {

    protected BaseStuffOwner() {
    }

    protected BaseStuffOwner(long playerId, IM model) {
        super(playerId, model);
    }

    /**
     * 扣除事物
     *
     * @param tradeItem  交易对象
     * @param action     交易行为
     * @param attributes 参数
     */
    protected abstract void deduct(TradeItem<SM> tradeItem, Action action, Attributes attributes);

    /**
     * 添加事物
     *
     * @param tradeItem  交易对象
     * @param action     交易行为
     * @param attributes 参数
     */
    protected abstract void reward(TradeItem<SM> tradeItem, Action action, Attributes attributes);

    /**
     * 添加 Item
     *
     * @param stuffs
     */
    protected abstract void setStuffs(Collection<S> stuffs);

}
