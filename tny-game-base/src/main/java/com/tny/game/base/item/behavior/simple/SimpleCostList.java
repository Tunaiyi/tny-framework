package com.tny.game.base.item.behavior.simple;

import com.tny.game.base.item.*;
import com.tny.game.base.item.behavior.*;

import java.util.*;

/**
 * 奖励列表
 *
 * @author KGTny
 */
public class SimpleCostList implements CostList {

    /**
     * 消耗所属的action
     */
    public Action action;

    /**
     * 消耗对象
     */
    public List<TradeItem<ItemModel>> itemList;

    /**
     * 奖励物品ID
     */
    public Set<ItemModel> tradeModelSet;

    public SimpleCostList(Action action) {
        this.action = action;
        this.itemList = Collections.emptyList();
        this.tradeModelSet = Collections.emptySet();
    }

    public SimpleCostList(Action action, List<TradeItem<ItemModel>> tradeItemList) {
        this.action = action;
        this.tradeModelSet = new HashSet<ItemModel>();
        for (TradeItem<ItemModel> item : tradeItemList) {
            tradeModelSet.add(item.getItemModel());
        }
        this.itemList = Collections.unmodifiableList(tradeItemList);
        this.tradeModelSet = Collections.unmodifiableSet(tradeModelSet);
    }

    public Action getAction() {
        return action;
    }

    @Override
    public Set<ItemModel> getTradeModelSet() {
        return tradeModelSet;
    }

    @Override
    public List<TradeItem<ItemModel>> getAwardTradeItemList() {
        return itemList;
    }

}
