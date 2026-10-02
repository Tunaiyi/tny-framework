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
