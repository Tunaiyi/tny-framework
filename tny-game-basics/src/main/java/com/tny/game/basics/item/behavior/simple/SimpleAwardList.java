package com.tny.game.basics.item.behavior.simple;

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.*;

import java.util.*;

/**
 * 奖励列表
 *
 * @author KGTny
 */
public class SimpleAwardList implements AwardList {

    public Action action;

    /**
     * 奖励信息 <奖励组<物品ID, 数量>>
     */
    public List<AwardDetail> tradeDetailList;

    /**
     * 奖励物品ID
     */
    public Set<ItemModel> tradeModelSet;

    public SimpleAwardList(Action action) {
        this.action = action;
        this.tradeDetailList = Collections.emptyList();
        this.tradeModelSet = Collections.unmodifiableSet(new HashSet<ItemModel>());
    }

    public SimpleAwardList(Action action, List<AwardDetail> awardList) {
        this.action = action;
        this.tradeDetailList = Collections.unmodifiableList(awardList);
        this.tradeModelSet = new HashSet<ItemModel>();
        for (AwardDetail detail : awardList) {
            for (TradeItem<ItemModel> item : detail.getAllTradeItemList())
                tradeModelSet.add(item.getItemModel());
        }
        this.tradeModelSet = Collections.unmodifiableSet(tradeModelSet);
    }

    public Action getAction() {
        return action;
    }

    @Override
    public List<AwardDetail> getAwardDetailList() {
        return tradeDetailList;
    }

    @Override
    public Set<ItemModel> getAwardItemModelSet() {
        return tradeModelSet;
    }

    @Override
    public List<TradeItem<ItemModel>> getAwardTradeItemList() {
        if (tradeDetailList.size() == 1)
            tradeDetailList.get(0).getAllTradeItemList();
        List<TradeItem<ItemModel>> itemList = new LinkedList<TradeItem<ItemModel>>();
        for (AwardDetail detail : tradeDetailList)
            itemList.addAll(detail.getAllTradeItemList());
        return itemList;
    }

}
