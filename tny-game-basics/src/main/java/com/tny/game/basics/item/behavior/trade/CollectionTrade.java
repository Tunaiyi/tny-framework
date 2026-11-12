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

package com.tny.game.basics.item.behavior.trade;

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.*;
import com.tny.game.basics.item.behavior.simple.*;
import com.tny.game.common.number.*;

import java.util.*;

public class CollectionTrade implements Trade {

    private final Action action;

    private final TradeType tradeType;

    private final Map<Integer, CollectTradeItem> tradeMap;

    public CollectionTrade(Action action, TradeType tradeType) {
        this.action = action;
        this.tradeType = tradeType;
        this.tradeMap = new HashMap<>();
    }

    public CollectionTrade(Action action, TradeType tradeType, Trade... trades) {
        this.tradeMap = new HashMap<>();
        this.action = action;
        this.tradeType = tradeType;
        for (Trade trade : trades) {
            this.collectTrade(trade);
        }
    }

    public CollectionTrade(Action action, TradeType tradeType, List<? extends TradeItem<?>> tradeItemList) {
        this.tradeMap = new HashMap<>();
        this.action = action;
        this.tradeType = tradeType;
        this.collectItem(tradeItemList);
    }

    public void collectItem(StuffModel model, Number number) {
        this.collectItem(new SimpleTradeItem<>(model, number));
    }

    public void collectItem(StuffModel model, Number number, AlterType alertType) {
        this.collectItem(new SimpleTradeItem<>(model, number, alertType));
    }

    public void collectItem(TradeItem<?>... tradeItems) {
        this.collectItem(Arrays.asList(tradeItems));
    }

    private void collectItem(Collection<? extends TradeItem<?>> tradeItemCollection) {
        for (TradeItem<?> tradeItem : tradeItemCollection) {
            CollectTradeItem item = this.tradeMap.get(tradeItem.getItemModel().getId());
            if (item != null) {
                item.collect(tradeItem);
            } else {
                this.tradeMap.put(tradeItem.getItemModel().getId(), new CollectTradeItem(tradeItem));
            }
        }
    }

    public void collectTrade(Trade trade) {
        if (trade.getTradeType() == this.tradeType) {
            this.collectItem(trade.getAllTradeItems());
        }
    }

    @Override
    public Action getAction() {
        return this.action;
    }

    @Override
    public TradeType getTradeType() {
        return this.tradeType;
    }

    @Override
    public Number getNumber(StuffModel model) {
        TradeItem<StuffModel> tradeItem = this.tradeMap.get(model.getId());
        if (tradeItem == null) {
            return 0;
        }
        return tradeItem.getNumber();
    }

    @Override
    public boolean isNeedTrade(StuffModel model) {
        return NumberAide.greater(this.getNumber(model), 0);
    }

    @Override
    public List<TradeItem<StuffModel>> getAllTradeItems() {
        return new ArrayList<>(this.tradeMap.values());
    }

    @Override
    public boolean isEmpty() {
        return this.tradeMap.isEmpty();
    }

    @Override
    public Collection<TradeItem<StuffModel>> getTradeItemsBy(ItemType... itemType) {
        return this.getTradeItemsBy(Arrays.asList(itemType));
    }

    @Override
    public Collection<TradeItem<StuffModel>> getTradeItemsBy(Collection<ItemType> itemType) {
        List<TradeItem<StuffModel>> tradeItemList = new ArrayList<>();
        for (TradeItem<StuffModel> tradeItem : this.getAllTradeItems()) {
            if (tradeItem.getItemModel().getItemType() == itemType) {
                tradeItemList.add(tradeItem);
            }
        }
        return tradeItemList;
    }

    @Override
    public boolean has(ItemType... itemTypes) {
        for (TradeItem<StuffModel> tradeItem : this.tradeMap.values()) {
            ItemType type = tradeItem.getItemModel().getItemType();
            for (ItemType itemType : itemTypes) {
                if (itemType == type) {
                    return true;
                }
            }
        }
        return false;
    }

}