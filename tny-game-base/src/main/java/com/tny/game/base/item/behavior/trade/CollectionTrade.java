package com.tny.game.base.item.behavior.trade;

import com.tny.game.base.item.ItemModel;
import com.tny.game.base.item.ItemType;
import com.tny.game.base.item.Trade;
import com.tny.game.base.item.TradeItem;
import com.tny.game.base.item.behavior.Action;
import com.tny.game.base.item.behavior.TradeType;

import java.util.*;

public class CollectionTrade implements Trade {

    private Action action;

    private TradeType tradeType;

    private Map<Integer, CollectionTradeItem> tradeMap;

    public CollectionTrade(Action action, TradeType tradeType) {
        this.action = action;
        this.tradeType = tradeType;
        this.tradeMap = new HashMap<Integer, CollectionTradeItem>();
    }

    public CollectionTrade(Action action, TradeType tradeType, Trade... trades) {
        this.tradeMap = new HashMap<Integer, CollectionTradeItem>();
        this.action = action;
        this.tradeType = tradeType;
        for (Trade trade : trades) {
            this.addTrade(trade);
        }
    }

    private void mergerItem(Collection<TradeItem<ItemModel>> tradeItemCollection) {
        for (TradeItem<ItemModel> tradeItem : tradeItemCollection) {
            CollectionTradeItem item = this.tradeMap.get(tradeItem.getItemModel().getID());
            if (item != null) {
                item.collect(tradeItem);
            } else {
                this.tradeMap.put(tradeItem.getItemModel().getID(), new CollectionTradeItem(tradeItem));
            }
        }
    }

    public void addTrade(Trade trade) {
        if (trade.getTradeType() == this.tradeType) {
            this.mergerItem(trade.getAllTradeItem());
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
    public int getNumber(ItemModel model) {
        TradeItem<ItemModel> tradeItem = this.tradeMap.get(model.getID());
        if (tradeItem == null)
            return 0;
        return tradeItem.getNumber();
    }

    @Override
    public boolean isNeedTrade(ItemModel model) {
        return this.getNumber(model) > 0;
    }

    @Override
    public List<TradeItem<ItemModel>> getAllTradeItem() {
        return new ArrayList<TradeItem<ItemModel>>(this.tradeMap.values());
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
        List<TradeItem<ItemModel>> tradeItemList = new ArrayList<TradeItem<ItemModel>>();
        for (TradeItem<ItemModel> tradeItem : this.getAllTradeItem()) {
            if (tradeItem.getItemModel().getItemType() == itemType) {
                tradeItemList.add(tradeItem);
            }
        }
        return tradeItemList;
    }

    @Override
    public void merge() {
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