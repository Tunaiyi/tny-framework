package com.tny.game.basics.item.behavior.simple;

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.*;

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
    public List<TradeItem<StuffModel>> itemList;

    /**
     * 奖励物品ID
     */
    public Set<StuffModel> tradeModelSet;

    public SimpleCostList(Action action) {
        this.action = action;
        this.itemList = Collections.emptyList();
        this.tradeModelSet = Collections.emptySet();
    }

    public SimpleCostList(Action action, List<TradeItem<StuffModel>> tradeItemList) {
        this.action = action;
        this.tradeModelSet = new HashSet<>();
        for (TradeItem<StuffModel> item : tradeItemList) {
            tradeModelSet.add(item.getItemModel());
        }
        this.itemList = Collections.unmodifiableList(tradeItemList);
        this.tradeModelSet = Collections.unmodifiableSet(tradeModelSet);
    }

    public Action getAction() {
        return action;
    }

    @Override
    public Set<StuffModel> getTradeModelSet() {
        return tradeModelSet;
    }

    @Override
    public List<TradeItem<StuffModel>> getAwardTradeItemList() {
        return itemList;
    }

}
