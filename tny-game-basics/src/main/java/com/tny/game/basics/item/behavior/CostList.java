package com.tny.game.basics.item.behavior;

import com.tny.game.basics.item.*;

import java.util.*;

/**
 * 奖励列表,记录奖励的所有物品
 *
 * @author KGTny
 */
public interface CostList {

    Action getAction();

    List<TradeItem<StuffModel>> getAwardTradeItemList();

    Set<StuffModel> getTradeModelSet();

}
