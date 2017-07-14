package com.tny.game.base.item.behavior.simple;

import com.tny.game.base.item.AlterType;
import com.tny.game.base.item.ItemModel;
import com.tny.game.base.item.ItemType;
import com.tny.game.base.item.Trade;
import com.tny.game.base.item.TradeInfo;
import com.tny.game.base.item.TradeItem;
import com.tny.game.base.item.behavior.Action;
import com.tny.game.base.item.behavior.TradeType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.tny.game.common.number.NumberAide.*;

public class SimpleTrade implements Trade {

    protected Action action;
    protected TradeType tradeType;
    protected List<TradeItem<ItemModel>> tradeItemList = new ArrayList<>();

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
                if (greater(item.getNumber(), 0))
                    this.tradeItemList.add((TradeItem<ItemModel>) item);
            }
        }
        //		this.tradeItemList = Collections.unmodifiableList(this.tradeItemList);
    }

    public void addIteam(ItemModel itemModel, Number number, AlterType alertType) {
        this.tradeItemList.add(new SimpleTradeItem<>(itemModel, number, alertType));
    }

    public void addIteam(ItemModel itemModel, Number number) {
        this.tradeItemList.add(new SimpleTradeItem<>(itemModel, number));
    }

    @SuppressWarnings("unchecked")
    public SimpleTrade(Action action, TradeType tradeType, Collection<? extends TradeItem<?>> tradeItemList) {
        this.action = action;
        this.tradeType = tradeType;
        if (tradeItemList != null && tradeItemList.size() > 0) {
            this.tradeItemList.addAll(tradeItemList.stream().filter(item -> greater(item.getNumber(), 0)).map(item -> (TradeItem<ItemModel>) item).collect(Collectors.toList()));
        }
        this.tradeItemList = Collections.unmodifiableList(this.tradeItemList);
    }

    @SuppressWarnings("unchecked")
    public SimpleTrade(TradeInfo info) {
        this.action = info.getAction();
        this.tradeType = info.getTradeType();
        this.tradeItemList = new ArrayList<>(info.getAllTradeItem());
        Collection<TradeItem<ItemModel>> tradeItems = info.getAllTradeItem();
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
    public Number getNumber(ItemModel model) {
        Number number = 0;
        for (TradeItem<ItemModel> item : this.tradeItemList) {
            if (item.getItemModel().equals(model))
                number = add(number, item.getNumber());
        }
        return number;
    }

    @Override
    public boolean isNeedTrade(ItemModel model) {
        return greater(this.getNumber(model), 0);
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
            if (greater(item.getNumber(), 0))
                return false;
        return true;
    }

    @Override
    public Collection<TradeItem<ItemModel>> getTradeItemBy(ItemType... itemType) {
        return this.getTradeItemBy(Arrays.asList(itemType));
    }

    @Override
    public Collection<TradeItem<ItemModel>> getTradeItemBy(Collection<ItemType> itemType) {
        List<TradeItem<ItemModel>> tradeItemList = this.getAllTradeItem().stream().filter(tradeItem -> itemType.contains(tradeItem.getItemModel().getItemType())).collect(Collectors.toList());
        return tradeItemList;
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
