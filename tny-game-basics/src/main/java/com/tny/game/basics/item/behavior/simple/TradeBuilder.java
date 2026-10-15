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

package com.tny.game.basics.item.behavior.simple;

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.*;

import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/25 12:10 下午
 */
public class TradeBuilder {

    private Action action;

    private TradeType tradeType;

    private List<TradeItem<?>> tradeItems = new ArrayList<>();

    public static TradeBuilder awardBuilder(Action action) {
        return new TradeBuilder(action, TradeType.AWARD);
    }

    public static TradeBuilder costBuilder(Action action) {
        return new TradeBuilder(action, TradeType.DEDUCT);
    }

    private TradeBuilder(Action action, TradeType tradeType) {
        this.action = action;
        this.tradeType = tradeType;
    }

    public TradeBuilder addItem(StuffModel itemModel, Number number, AlterType alertType) {
        this.tradeItems.add(new SimpleTradeItem<>(itemModel, number, alertType));
        return this;
    }

    public TradeBuilder addItem(StuffModel itemModel, Number number) {
        this.tradeItems.add(new SimpleTradeItem<>(itemModel, number));
        return this;
    }

    public TradeBuilder addItem(TradeItem<?> item) {
        this.tradeItems.add(item);
        return this;
    }

    public TradeBuilder addItem(TradeItem<?>... items) {
        for (TradeItem<?> item : items)
            this.tradeItems.add(item);
        return this;
    }

    public TradeBuilder addItems(Collection<TradeItem<?>> items) {
        this.tradeItems.addAll(items);
        return this;
    }

    public Trade build() {
        return new SimpleTrade(action, tradeType, this.tradeItems);
    }

}
