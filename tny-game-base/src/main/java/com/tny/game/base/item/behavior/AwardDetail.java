package com.tny.game.base.item.behavior;

import com.tny.game.base.item.ItemModel;
import com.tny.game.base.item.TradeItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AwardDetail {

    private List<TradeItem<ItemModel>> itemList = new ArrayList<TradeItem<ItemModel>>();

    protected int probability;

    public AwardDetail(int probability, List<TradeItem<ItemModel>> itemList) {
        this.itemList.addAll(itemList);
        this.probability = probability;
        this.itemList = Collections.unmodifiableList(this.itemList);
    }

    public List<TradeItem<ItemModel>> getAllTradeItemList() {
        return itemList;
    }

    public int getProbability() {
        return probability;
    }

}
