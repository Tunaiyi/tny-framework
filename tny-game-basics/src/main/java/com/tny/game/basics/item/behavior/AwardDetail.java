package com.tny.game.basics.item.behavior;

import com.tny.game.basics.item.*;

import java.util.*;

public class AwardDetail {

    private List<TradeItem<StuffModel>> itemList = new ArrayList<>();

    public AwardDetail(List<TradeItem<StuffModel>> itemList) {
        this.itemList.addAll(itemList);
        this.itemList = Collections.unmodifiableList(this.itemList);
    }

    public List<TradeItem<StuffModel>> getAllTradeItemList() {
        return itemList;
    }

}
