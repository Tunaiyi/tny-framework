package com.tny.game.basics.item.behavior;

import com.tny.game.basics.item.*;

import java.util.*;

public class AwardDetail {

    private List<TradeItem<ItemModel>> itemList = new ArrayList<TradeItem<ItemModel>>();


    public AwardDetail(List<TradeItem<ItemModel>> itemList) {
        this.itemList.addAll(itemList);
        this.itemList = Collections.unmodifiableList(this.itemList);
    }

    public List<TradeItem<ItemModel>> getAllTradeItemList() {
        return itemList;
    }

}
