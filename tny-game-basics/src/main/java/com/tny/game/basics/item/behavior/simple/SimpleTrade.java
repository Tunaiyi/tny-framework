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

package com.tny.game.basics.item.behavior.simple;

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.tny.game.common.number.NumberAide.*;

public class SimpleTrade implements Trade {

    protected Action action;

    protected TradeType tradeType;

    protected List<TradeItem<StuffModel>> tradeItemList = new ArrayList<>();

    public SimpleTrade(Action action, TradeType tradeType) {
        this.action = action;
        this.tradeType = tradeType;
    }

    @SuppressWarnings("unchecked")
    public SimpleTrade(Action action, TradeType tradeType, TradeItem<?>... tradeItemList) {
        this.action = action;
        this.tradeType = tradeType;
        if (tradeItemList != null && tradeItemList.length > 0) {
            for (TradeItem<?> item : tradeItemList) {
                if (greater(item.getNumber(), 0)) {
                    this.tradeItemList.add((TradeItem<StuffModel>) item);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public SimpleTrade(Action action, TradeType tradeType, Collection<? extends TradeItem<?>> tradeItemList) {
        this.action = action;
        this.tradeType = tradeType;
        if (tradeItemList != null && tradeItemList.size() > 0) {
            this.tradeItemList.addAll(tradeItemList.stream().filter(item -> greater(item.getNumber(), 0)).map(item -> (TradeItem<StuffModel>) item)
                    .collect(Collectors.toList()));
        }
        this.tradeItemList = Collections.unmodifiableList(this.tradeItemList);
    }

    @SuppressWarnings("unchecked")
    public SimpleTrade(TradeInfo info) {
        this.action = info.getAction();
        this.tradeType = info.getTradeType();
        this.tradeItemList = new ArrayList<>(info.getAllTradeItems());
        Collection<TradeItem<StuffModel>> tradeItems = info.getAllTradeItems();
        if (tradeItems != null && tradeItems.size() > 0) {
            this.tradeItemList.addAll(tradeItems.stream().filter(item -> greater(item.getNumber(), 0)).collect(Collectors.toList()));
        }
        this.tradeItemList = Collections.unmodifiableList(this.tradeItemList);
    }

    @Override
    public TradeType getTradeType() {
        return this.tradeType;
    }

    @Override
    public Number getNumber(StuffModel model) {
        Number number = 0;
        for (TradeItem<StuffModel> item : this.tradeItemList) {
            if (item.getItemModel().equals(model)) {
                number = add(number, item.getNumber());
            }
        }
        return number;
    }

    @Override
    public boolean isNeedTrade(StuffModel model) {
        return greater(this.getNumber(model), 0);
    }

    @Override
    public Action getAction() {
        return this.action;
    }

    @Override
    public List<TradeItem<StuffModel>> getAllTradeItems() {
        return Collections.unmodifiableList(this.tradeItemList);
    }

    @Override
    public String toString() {
        return "SimpleTradeResult [tradeType=" + this.tradeType + ", tradeItemList=" + this.tradeItemList + "]";
    }

    @Override
    public boolean isEmpty() {
        for (TradeItem<?> item : this.tradeItemList)
            if (greater(item.getNumber(), 0)) {
                return false;
            }
        return true;
    }

    @Override
    public Collection<TradeItem<StuffModel>> getTradeItemsBy(ItemType... itemType) {
        return this.getTradeItemsBy(Arrays.asList(itemType));
    }

    @Override
    public Collection<TradeItem<StuffModel>> getTradeItemsBy(Collection<ItemType> itemType) {
        List<TradeItem<StuffModel>> tradeItemList = this.getAllTradeItems().stream()
                .filter(tradeItem -> itemType.contains(tradeItem.getItemModel().getItemType()))
                .collect(Collectors.toList());
        return tradeItemList;
    }

    @Override
    public boolean has(ItemType... itemTypes) {
        for (TradeItem<StuffModel> tradeItem : this.tradeItemList) {
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
