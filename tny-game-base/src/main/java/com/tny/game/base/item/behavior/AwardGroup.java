package com.tny.game.base.item.behavior;

import com.tny.game.base.item.*;
import com.tny.game.base.item.probability.*;

import java.util.*;

/**
 * 奖励物品组
 *
 * @author KGTny
 */
public interface AwardGroup extends Probability, ProbabilityGroup<Award> {

    /**
     * 获取奖励组所奖励的所有物品ID列表
     *
     * @return
     */
    Set<ItemModel> getAwardSet(Map<String, Object> attributeMap);

    /**
     * 计算该组奖励物品和数量
     *
     * @param attributeMap 计算参数
     * @return 返回奖励的物品和数量
     */
    List<TradeItem<ItemModel>> countAwardNumber(boolean merge, Map<String, Object> attributeMap);

    /**
     * 创建奖励结果集
     *
     * @param playerID     玩家id
     * @param action       奖励的操作类型
     * @param attributeMap 附加参数
     * @return 返回结果集
     */
    List<TradeItem<ItemModel>> countAwardResult(long playerID, Action action, boolean merge, Map<String, Object> attributeMap);

}
