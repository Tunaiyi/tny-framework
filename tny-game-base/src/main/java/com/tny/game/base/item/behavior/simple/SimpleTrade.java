package com.tny.game.base.item.behavior.simple;

import com.tny.game.base.item.*;
import com.tny.game.base.item.behavior.Action;
import com.tny.game.base.item.behavior.TradeType;

import java.util.*;
import java.util.Map.Entry;

public class SimpleTrade implements Trade {

    protected Action action;
    protected TradeType tradeType;
    protected List<TradeItem<ItemModel>> tradeItemList = new ArrayList<TradeItem<ItemModel>>();

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
                if (item.getNumber() > 0)
                    this.tradeItemList.add((TradeItem<ItemModel>) item);
            }
        }
        //		this.tradeItemList = Collections.unmodifiableList(this.tradeItemList);
    }

    public void addIteam(ItemModel itemModel, int number, AlterType alertType) {
        this.tradeItemList.add(new SimpleTradeItem<ItemModel>(itemModel, number, alertType));
    }

    public void addIteam(ItemModel itemModel, int number) {
        this.tradeItemList.add(new SimpleTradeItem<ItemModel>(itemModel, number));
    }

    @SuppressWarnings("unchecked")
    public SimpleTrade(Action action, TradeType tradeType, List<? extends TradeItem<?>> tradeItemList) {
        this.action = action;
        this.tradeType = tradeType;
        if (tradeItemList != null && tradeItemList.size() > 0) {
            for (TradeItem<?> item : tradeItemList) {
                if (item.getNumber() > 0)
                    this.tradeItemList.add((TradeItem<ItemModel>) item);
            }
        }
        this.tradeItemList = Collections.unmodifiableList(this.tradeItemList);
    }

    @Override
    public TradeType getTradeType() {
        return this.tradeType;
    }

    @Override
    public int getNumber(ItemModel model) {
        int number = 0;
        for (TradeItem<ItemModel> item : this.tradeItemList) {
            if (item.getItemModel().equals(model))
                number += item.getNumber();
        }
        return number;
    }

    @Override
    public boolean isNeedTrade(ItemModel model) {
        return this.getNumber(model) > 0;
    }

    @Override
    public Action getAction() {
        return this.action;
    }

    @Override
    public List<TradeItem<ItemModel>> getAllTradeItem() {
        return Collections.unmodifiableList(this.tradeItemList);
    }

    @Override
    public String toString() {
        return "SimpleTradeResult [tradeType=" + this.tradeType + ", tradeItemList=" + this.tradeItemList + "]";
    }

    @Override
    public boolean isEmpty() {
        for (TradeItem<?> item : this.tradeItemList)
            if (item.getNumber() > 0)
                return false;
        return true;
    }

    @Override
    public Collection<TradeItem<ItemModel>> getTradeItemBy(ItemType... itemType) {
        return this.getTradeItemBy(Arrays.asList(itemType));
    }

    @Override
    public Collection<TradeItem<ItemModel>> getTradeItemBy(Collection<ItemType> itemType) {
        List<TradeItem<ItemModel>> tradeItemList = new ArrayList<TradeItem<ItemModel>>();
        for (TradeItem<ItemModel> tradeItem : this.getAllTradeItem()) {
            if (itemType.contains(tradeItem.getItemModel().getItemType())) {
                tradeItemList.add(tradeItem);
            }
        }
        return tradeItemList;
    }

    @Override
    public void merge() {
        Map<ItemModel, Integer> itemNumMap = new HashMap<ItemModel, Integer>();
        for (TradeItem<ItemModel> item : this.tradeItemList) {
            Integer value = itemNumMap.get(item.getItemModel());
            if (value == null)
                value = 0;
            value += item.getNumber();
            itemNumMap.put(item.getItemModel(), value);
        }
        List<TradeItem<ItemModel>> tradeItemList = new ArrayList<TradeItem<ItemModel>>();
        for (Entry<ItemModel, Integer> entry : itemNumMap.entrySet())
            tradeItemList.add(new SimpleTradeItem<ItemModel>(entry.getKey(), entry.getValue()));
        this.tradeItemList = tradeItemList;
    }

    @Override
    public boolean has(ItemType... itemTypes) {
        for (TradeItem<ItemModel> tradeItem : this.tradeItemList) {
            ItemType type = tradeItem.getItemModel().getItemType();
            for (ItemType itemType : itemTypes) {
                if (itemType == type)
                    return true;
            }
        }
        return false;
    }
}
