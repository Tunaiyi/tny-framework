package com.tny.game.basics.item.behavior;

import com.tny.game.basics.exception.*;
import com.tny.game.basics.item.*;

import java.util.*;

/**
 * 行为操作方案
 *
 * @author KGTny
 */
public interface ActionPlan {

    /**
     * 获取附加参数
     *
     * @return
     */
    Set<String> getAttributesAliasSet();

    /**
     * 获取该操作的操作类
     *
     * @return
     */
    Set<Action> getActions();

    /**
     * 获取该操作的条件列表
     *
     * @return
     */
    List<DemandResult> countDemandResult(long playerId, Map<String, Object> map);

    /**
     * 尝试做该操作,一遇到失败的条件则马上返回
     *
     * @param playerId
     * @param attributes 计算参数
     * @return 返回未达到条件的结果集, 若尝试成功则返回null
     * @throws TryToDoException
     */
    DemandResultCollector tryToDo(long playerId, boolean tryAll, DemandResultCollector collector, Map<String, Object> attributes);

    /**
     * 获取操作结果
     *
     * @return
     */
    ActionResult getActionResult(long playerId, Action action, Map<String, Object> attributes);

    /**
     * 获取奖励物品列表
     *
     * @return
     */
    AwardList getAwardList(long playerId, Action action, Map<String, Object> attributes);

    /**
     * 获取奖励物品列表
     *
     * @return
     */
    CostList getCostList(long playerId, Action action, Map<String, Object> attributes);

    /**
     * 获取该操作的奖励/消耗结果
     *
     * @param attributes
     * @return
     */
    Trade createAward(long playerId, Action action, Map<String, Object> attributes);

    /**
     * 获取该操作的奖励/消耗结果
     *
     * @param attributes
     * @return
     */
    Trade createCost(long playerId, Action action, Map<String, Object> attributes);

    /**
     * 获取该操作的奖励&扣除结果
     *
     * @param action       指定操作类型
     * @param attributeMap 附加参数
     * @return 返回奖励列表
     */
    ActionTrades countTrades(long playerId, Action action, Map<String, Object> attributeMap);

    /**
     * 计算选项
     *
     * @param option
     * @param attributes
     * @return
     */
    <O> O countOption(long playerId, Option option, Map<String, Object> attributes);

    /**
     * 是否存在选项
     *
     * @param option
     * @return
     */
    boolean isHasOption(Option option);

}