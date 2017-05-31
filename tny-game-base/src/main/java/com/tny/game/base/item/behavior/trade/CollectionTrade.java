package com.tny.game.base.item.behavior.trade;

import com.tny.game.base.item.AlterType;
import com.tny.game.base.item.ItemModel;
import com.tny.game.base.item.ItemType;
import com.tny.game.base.item.Trade;
import com.tny.game.base.item.TradeItem;
import com.tny.game.base.item.behavior.Action;
import com.tny.game.base.item.behavior.TradeType;
import com.tny.game.base.item.behavior.simple.SimpleTradeItem;
import com.tny.game.number.NumberUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectionTrade implements Trade {

    private Action action;

    private TradeType tradeType;

    private Map<Integer, CollectionTradeItem> tradeMap;

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

    public void collectItem(ItemModel model, Number number) {
        this.collectItem(new SimpleTradeItem<>(model, number));
    }

    public void collectItem(ItemModel model, Number number, AlterType alertType) {
        this.collectItem(new SimpleTradeItem<>(model, number, alertType));
    }

    public void collectItem(TradeItem<?>... tradeItems) {
        this.collectItem(Arrays.asList(tradeItems));
    }

    private void collectItem(Collection<? extends TradeItem<?>> tradeItemCollection) {
        for (TradeItem<?> tradeItem : tradeItemCollection) {
            CollectionTradeItem item = this.tradeMap.get(tradeItem.getItemModel().getID());
            if (item != null) {
                item.collect(tradeItem);
            } else {
                this.tradeMap.put(tradeItem.getItemModel().getID(), new CollectionTradeItem(tradeItem));
            }
        }
    }

    public void collectTrade(Trade trade) {
        if (trade.getTradeType() == this.tradeType) {
            this.collectItem(trade.getAllTradeItem());
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
    public Number getNumber(ItemModel model) {
        TradeItem<ItemModel> tradeItem = this.tradeMap.get(model.getID());
        if (tradeItem == null)
            return 0;
        return tradeItem.getNumber();
    }

    @Override
    public boolean isNeedTrade(ItemModel model) {
        return NumberUtils.greater(this.getNumber(model), 0);
    }

    @Override
    public List<TradeItem<ItemModel>> getAllTradeItem() {
        return new ArrayList<>(this.tradeMap.values());
    }

    @Override
    public boolean isEmpty() {
        return this.tradeMap.isEmpty();
    }

    @Override
    public Collection<TradeItem<ItemModel>> getTradeItemBy(ItemType... itemType) {
        return this.getTradeItemBy(Arrays.asList(itemType));
    }

    @Override
    public Collection<TradeItem<ItemModel>> getTradeItemBy(Collection<ItemType> itemType) {
        List<TradeItem<ItemModel>> tradeItemList = new ArrayList<>();
        for (TradeItem<ItemModel> tradeItem : this.getAllTradeItem()) {
            if (tradeItem.getItemModel().getItemType() == itemType) {
                tradeItemList.add(tradeItem);
            }
        }
        return tradeItemList;
    }

    @Override
    public Trade merge() {
        return this;
    }

    @Override
    public boolean has(ItemType... itemTypes) {
        for (TradeItem<ItemModel> tradeItem : this.tradeMap.values()) {
            ItemType type = tradeItem.getItemModel().getItemType();
            for (ItemType itemType : itemTypes) {
                if (itemType == type)
                    return true;
            }
        }
        return false;
    }
}