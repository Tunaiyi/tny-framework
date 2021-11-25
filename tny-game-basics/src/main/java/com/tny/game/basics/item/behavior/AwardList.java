package com.tny.game.basics.item.behavior;

import com.tny.game.basics.item.*;

import java.util.*;

/**
 * 奖励列表,记录奖励的所有物品
 *
 * @author KGTny
 */
public interface AwardList {

	Action getAction();

	/**
	 * 获取奖励的所有<奖励组<奖励物品的ID - 奖励数量>>
	 *
	 * @return
	 */
	List<AwardDetail> getAwardDetailList();

	List<TradeItem<StuffModel>> getAwardTradeItemList();

	Set<StuffModel> getAwardItemModelSet();

}
