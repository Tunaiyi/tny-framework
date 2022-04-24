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
    public Set<StuffModel> tradeModelSet;

    public SimpleAwardList(Action action) {
        this.action = action;
        this.tradeDetailList = Collections.emptyList();
        this.tradeModelSet = Collections.unmodifiableSet(new HashSet<>());
    }

    public SimpleAwardList(Action action, List<AwardDetail> awardList) {
        this.action = action;
        this.tradeDetailList = Collections.unmodifiableList(awardList);
        this.tradeModelSet = new HashSet<>();
        for (AwardDetail detail : awardList) {
            for (TradeItem<StuffModel> item : detail.getAllTradeItemList())
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
    public Set<StuffModel> getAwardItemModelSet() {
        return tradeModelSet;
    }

    @Override
    public List<TradeItem<StuffModel>> getAwardTradeItemList() {
        if (tradeDetailList.size() == 1) {
            tradeDetailList.get(0).getAllTradeItemList();
        }
        List<TradeItem<StuffModel>> itemList = new LinkedList<>();
        for (AwardDetail detail : tradeDetailList)
            itemList.addAll(detail.getAllTradeItemList());
        return itemList;
    }

}
