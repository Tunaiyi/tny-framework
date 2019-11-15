package com.tny.game.base.item.behavior;

import com.tny.game.base.item.*;

import java.util.*;

/**
 * 行为模型接口
 *
 * @author KGTny
 */
public interface BehaviorPlan {

    /**
     * 获取行为类型
     *
     * @return
     */
    Behavior getBehavior();

    /**
     * 获取附加参数
     *
     * @return
     */
    Set<String> getAttributesAliasSet();

    /**
     * 获取操作方案Map
     *
     * @return
     */
    Map<Action, ActionPlan> getActionPlanMap();

    /**
     * 通过操作类型获取操作方案
     *
     * @param action 指定的操作类型
     * @return
     */
    ActionPlan getActionPlan(Action action);

    DemandResultCollector tryToDo(long playerID, Action action, boolean tryAll, Map<String, Object> attributeMap);

    /**
     * 计算对该事物进行某操作对象操作的消费
     *
     * @param action       要执行的操作
     * @param attributeMap 附加参数 ["key1", object1, "key2", object2]
     * @return 返回获得
     */
    Trade countCost(long playerID, Action action, Map<String, Object> attributeMap);

    /**
     * 计算对该事物进行某操作对象操作的奖励
     *
     * @param action       要执行的操作
     * @param attributeMap 附加参数 ["key1", object1, "key2", object2]
     * @return 返回获得
     */
    Trade countAward(long playerID, Action action, Map<String, Object> attributeMap);

    /**
     * 计算对该事物进行某操作对象操作的奖励&扣除
     *
     * @param action       指定操作类型
     * @param attributeMap 附加参数
     * @return 返回奖励列表
     */
    ActionTrades countTrades(long playerID, Action action, Map<String, Object> attributeMap);

    /**
     * 获取制定操作类型的奖励列表
     *
     * @param action       指定操作类型
     * @param attributeMap 附加参数
     * @return 返回奖励列表
     */
    AwardList getAwardList(long playerID, Action action, Map<String, Object> attributeMap);

    /**
     * 获取制定操作类型的奖励列表
     *
     * @param action       指定操作类型
     * @param attributeMap 附加参数
     * @return 返回奖励列表
     */
    CostList getCostList(long playerID, Action action, Map<String, Object> attributeMap);

    /**
     * 计算行为结果
     *
     * @param attributeMap 附加参数
     * @return 返回行为结果
     */
    BehaviorResult countBehaviorResult(long playerID, Map<String, Object> attributeMap);

    /**
     * 获取行为中的条件结果集
     *
     * @param map 附加参数
     * @return 返回条件结果集列表
     */
    List<DemandResult> countAllDemandResults(long playerID, Map<String, Object> map);

    <O> O countOption(long playerID, Action action, Option option, Map<String, Object> attributes);

    boolean isHasOption(Action action, Option option);

    ActionResult getActionResult(long playerID, Action action, Map<String, Object> attributeMap);

}
