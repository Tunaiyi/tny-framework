package com.tny.game.base.item.behavior;

import com.tny.game.base.item.*;

import java.util.*;

/**
 * 奖励列表,记录奖励的所有物品
 *
 * @author KGTny
 */
public interface AwardList {

    public Action getAction();

    /**
     * 获取奖励的所有<奖励组<奖励物品的ID - 奖励数量>>
     *
     * @return
     */
    public List<AwardDetail> getAwardDetailList();

    public List<TradeItem<ItemModel>> getAwardTradeItemList();

    public Set<ItemModel> getAwardItemModelSet();

}
