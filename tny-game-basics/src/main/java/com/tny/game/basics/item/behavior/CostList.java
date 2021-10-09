package com.tny.game.basics.item.behavior;

import com.tny.game.basics.item.*;

import java.util.*;

/**
 * 奖励列表,记录奖励的所有物品
 *
 * @author KGTny
 */
public interface CostList {

    public Action getAction();

    public List<TradeItem<ItemModel>> getAwardTradeItemList();

    public Set<ItemModel> getTradeModelSet();

}
