package com.tny.game.base.item;

import com.tny.game.base.item.behavior.*;
import com.tny.game.expr.*;

import java.util.*;

/**
 * 游戏中所有事物的模型
 *
 * @author KGTny
 */
public interface ItemModel extends Model {

    /**
     * @return 事物类型
     */
    ItemType getItemType();

    /**
     * 尝试让某事物做某事,失败立即返回
     *
     * @param item       事物对象
     * @param action     操作
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 返回操作结果
     */
    TryToDoResult tryToDo(Item<?> item, Action action, Object... attributes);

    /**
     * 尝试让某事物做某事,失败立即返回
     *
     * @param item       事物对象
     * @param action     操作
     * @param award      是否创建奖励
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 返回操作结果
     */
    TryToDoResult tryToDo(Item<?> item, Action action, boolean award, Object... attributes);

    /**
     * 尝试让某事物做某事,失败立即返回
     *
     * @param playerID   玩家ID
     * @param action     操作
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 返回操作结果
     */
    TryToDoResult tryToDo(long playerID, Action action, Object... attributes);

    /**
     * 尝试让某事物做某事,失败立即返回
     *
     * @param playerID   玩家ID
     * @param action     操作
     * @param award      是否创建奖励
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 返回操作结果
     */
    TryToDoResult tryToDo(long playerID, Action action, boolean award, Object... attributes);

    /**
     * 尝试让某事物做某事,尝试所有条件后返回
     *
     * @param item       事物对象
     * @param action     操作
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 返回操作结果
     */
    TryToDoResult tryToDoAll(Item<?> item, Action action, Object... attributes);

    /**
     * 尝试让某事物做某事,尝试所有条件后返回
     *
     * @param item       事物对象
     * @param action     操作
     * @param award      是否创建奖励
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 返回操作结果
     */
    TryToDoResult tryToDoAll(Item<?> item, Action action, boolean award, Object... attributes);

    /**
     * 尝试让某事物做某事,尝试所有条件后返回
     *
     * @param playerID   玩家ID
     * @param action     操作
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 返回操作结果
     */
    TryToDoResult tryToDoAll(long playerID, Action action, Object... attributes);

    /**
     * 尝试让某事物做某事,尝试所有条件后返回
     *
     * @param playerID   玩家ID
     * @param action     操作
     * @param award      是否创建奖励
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 返回操作结果
     */
    TryToDoResult tryToDoAll(long playerID, Action action, boolean award, Object... attributes);

    //######################

    /**
     * 获取奖励&扣除列表
     *
     * @param item       事物对象
     * @param action     要执行的操作
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 奖励列表
     */
    ActionTrades createActionTrades(Item<?> item, Action action, Object... attributes);

    /**
     * 获取奖励&扣除列表
     *
     * @param playerID   玩家ID
     * @param action     要执行的才注意
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 奖励列表
     */
    ActionTrades createActionTrades(long playerID, Action action, Object... attributes);

    /**
     * 获取操作结果
     *
     * @param item       事物对象
     * @param action     操作
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 返回操作结果
     */
    ActionResult getActionResult(Item<?> item, Action action, Object... attributes);

    /**
     * 获取错做结果
     *
     * @param playerID
     * @param action
     * @param attributes
     * @return
     */
    ActionResult getActionResult(long playerID, Action action, Object... attributes);

    /**
     * 获取事物对象某操作的条件结果集
     *
     * @param item       事物对象
     * @param behavior   要执行的行为
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 返回该操作的条件结果
     */
    BehaviorResult getBehaviorResult(Item<?> item, Behavior behavior, Object... attributes);

    /**
     * 获取事物对象某操作的条件结果集
     *
     * @param playerID   玩家ID
     * @param behavior   要执行的行为
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 返回该操作的条件结果
     */
    BehaviorResult getBehaviorResult(long playerID, Behavior behavior, Object... attributes);

    /**
     * 获取奖励列表
     *
     * @param item       事物对象
     * @param action     要执行的操作
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 奖励列表
     */
    AwardList getAwardList(Item<?> item, Action action, Object... attributes);

    /**
     * 获取奖励列表
     *
     * @param playerID   玩家ID
     * @param action     要执行的才注意
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 奖励列表
     */
    AwardList getAwardList(long playerID, Action action, Object... attributes);

    /**
     * 获取奖励列表
     *
     * @param item       事物对象
     * @param action     要执行的才注意
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 消耗列表
     */
    CostList getCostList(Item<?> item, Action action, Object... attributes);

    /**
     * 获取奖励列表
     *
     * @param playerID   玩家ID
     * @param action     要执行的才注意
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 消耗列表
     */
    CostList getCostList(long playerID, Action action, Object... attributes);

    /**
     * 计算对该事物进行某操作对象操作的消费
     *
     * @param item       事物对象
     * @param action     要执行的操作
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 返回获得消耗交易对象
     */
    Trade createCostTrade(Item<?> item, Action action, Object... attributes);

    /**
     * 计算对该事物进行某操作对象操作的奖励
     *
     * @param item       事物对象
     * @param action     要执行的操作
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 返回获得奖励交易对象
     */
    Trade createAwardTrade(Item<?> item, Action action, Object... attributes);

    /**
     * 计算对该事物进行某操作对象操作的消费
     *
     * @param playerID   玩家ID
     * @param action     要执行的才注意
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 返回获得消耗交易对象
     */
    Trade createCostTrade(long playerID, Action action, Object... attributes);

    /**
     * 计算对该事物进行某操作对象操作的奖励
     *
     * @param playerID   玩家ID
     * @param action     要执行的才注意
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 返回获得奖励交易对象
     */
    Trade createAwardTrade(long playerID, Action action, Object... attributes);

    /**
     * 计算某事物某操作的选项值
     *
     * @param playerID   玩家ID
     * @param action     要执行的操作
     * @param option     选项类型
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 返回操作选项值
     */
    <O> O getActionOption(long playerID, Action action, Option option, Object... attributes);

    /**
     * 计算某事物某操作的选项值
     *
     * @param playerID   玩家ID
     * @param defaultNum 默认值
     * @param action     要执行的操作
     * @param option     选项类型
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 返回操作选项值
     */
    <O> O getActionOption(long playerID, O defaultNum, Action action, Option option, Object... attributes);

    /**
     * 计算某事物某操作的选项
     *
     * @param item       事物对象
     * @param action     要执行的操作
     * @param option     选项类型
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 返回操作选项值
     */
    <O> O getActionOption(Item<?> item, Action action, Option option, Object... attributes);

    /**
     * 计算某事物某操作的选项值
     *
     * @param item       事物对象
     * @param action     要执行的操作
     * @param option     选项类型
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 返回操作选项值
     */
    <O> O getActionOption(Item<?> item, O defaultNum, Action action, Option option, Object... attributes);

    /**
     * 计算某事物指定能力类型的能力值
     *
     * @param playerID   玩家ID
     * @param ability    能力值类型
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 返回能力值
     */
    <A> A getAbility(long playerID, Ability ability, Class<A> clazz, Object... attributes);

    /**
     * 计算某事物指定能力类型的能力值
     *
     * @param playerID      玩家ID
     * @param defaultObject 默认值
     * @param ability       能力值类型
     * @param attributes    附加参数 ["key1", object1, "key2", object2]
     * @return 返回能力值
     */
    <A> A getAbility(long playerID, A defaultObject, Ability ability, Object... attributes);

    /**
     * 计算某事物指定能力类型的能力值
     *
     * @param item       事物对象
     * @param ability    能力值类型
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 返回能力值
     */
    <A> A getAbility(Item<?> item, Ability ability, Class<A> clazz, Object... attributes);

    /**
     * 计算某事物指定能力类型的能力值
     *
     * @param item          事物对象
     * @param defaultObject 默认值
     * @param ability       能力值类型
     * @param attributes    附加参数 ["key1", object1, "key2", object2]
     * @return 返回能力值
     */
    <A> A getAbility(Item<?> item, A defaultObject, Ability ability, Object... attributes);

    /**
     * @return 获取所有Ability类型列表
     */
    Set<Ability> getAbilityTypes();

    /**
     * 获取指定的typeClass的Ability类型列表
     *
     * @param typeClass 指定Ability类
     * @return 返回typeClass的Ability类型列表
     */
    <A extends Ability> Set<A> getAbilityTypes(Class<A> typeClass);

    /**
     * 获取指定ability列表的的能力值
     *
     * @param item              事物对象
     * @param abilityCollection 能力值类型列表
     * @param attributes        附加参数 ["key1", object1, "key2", object2]
     * @return 返回指定ability列表的的能力值
     */
    <A> Map<Ability, A> getAbilities(Item<?> item, Collection<Ability> abilityCollection, Class<A> clazz, Object... attributes);

    /**
     * 获取指定ability列表的的能力值
     *
     * @param playerID          玩家ID
     * @param abilityCollection 能力值类型列表
     * @param attributes        附加参数 ["key1", object1, "key2", object2]
     * @return 返回指定ability列表的的能力值
     */
    <A> Map<Ability, A> getAbilities(long playerID, Collection<Ability> abilityCollection, Class<A> clazz, Object... attributes);

    /**
     * 获取指定类型的能力值
     *
     * @param item         事物对象
     * @param abilityClass 能力值类型Class
     * @param attributes   附加参数 ["key1", object1, "key2", object2]
     * @return 返回指定ability class的的能力值
     */
    <A extends Ability, V> Map<A, V> getAbilitiesByType(Item<?> item, Class<A> abilityClass, Class<V> clazz, Object... attributes);

    /**
     * 获取指定类型的能力值
     *
     * @param playerID     玩家ID
     * @param abilityClass 能力值类型Class
     * @param attributes   附加参数 ["key1", object1, "key2", object2]
     * @return 返回指定ability class的的能力值
     */
    <A extends Ability, V> Map<A, V> getAbilitiesByType(long playerID, Class<A> abilityClass, Class<V> clazz, Object... attributes);

    /**
     * 是否存在Ability
     *
     * @param ability 指定ability
     * @return true存在 false不存在
     */
    boolean hasAbility(Ability ability);

    /**
     * 获取指定类型的Ability类型
     *
     * @param abilityClass 指定的Ability
     * @return 指定类型的Ability类型
     */
    @SuppressWarnings("unchecked")
    Set<Ability> getOwnAbilityBy(Class<? extends Ability>... abilityClass);

    /**
     * 是否存在行为
     *
     * @param behavior 指定行为
     * @return true存在 false不存在
     */
    boolean hasBehavior(Behavior behavior);

    /**
     * 是否存在操作
     *
     * @param action 指定操作
     * @return true存在 false不存在
     */
    boolean hasAction(Action action);

    /**
     * 获取指定Action的Behavior
     *
     * @param action 指定Action
     * @return 返回Behavior
     */
    Behavior getBehaviorByAction(Action action);

    /**
     * 是否存在操作选项
     *
     * @param action 指定操作
     * @param option 指定操作选项
     * @return true存在 false不存在
     */
    boolean hasOption(Action action, Option option);

    /**
     * 当前值公式
     *
     * @return 返回当前值公式
     */
    Expr currentFormula();

    /**
     * 消耗条件公式
     *
     * @return 返回消耗条件公式
     */
    Expr demandFormula();

}
