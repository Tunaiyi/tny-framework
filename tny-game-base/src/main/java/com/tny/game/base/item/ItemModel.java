package com.tny.game.base.item;

import com.tny.game.base.exception.TryToDoException;
import com.tny.game.base.item.behavior.*;
import com.tny.game.common.formula.FormulaHolder;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * 游戏中所有事物的模型
 *
 * @author KGTny
 */
public interface ItemModel extends Model {

    /**
     * 获取描述
     *
     * @return
     */
    public String getDesc();

    /**
     * 事物类型
     *
     * @return
     */
    public <IT extends ItemType> IT getItemType();

    /**
     * 尝试让某事物做某事
     *
     * @param item      事物对象
     * @param action    操作
     * @param attribute 附加参数 ["key1", object1, "key2", object2]
     * @return 返回操作结果
     * @throws TryToDoException
     */
    public TryToDoResult tryToDo(Item<?> item, Action action, Object... attributes);

    /**
     * 尝试让某事物做某事
     *
     * @param item      事物对象
     * @param action    操作
     * @param award     是否创建奖励
     * @param attribute 附加参数 ["key1", object1, "key2", object2]
     * @return 返回操作结果
     * @throws TryToDoException
     */
    public TryToDoResult tryToDo(Item<?> item, Action action, boolean award, Object... attributes);

    /**
     * 尝试让某事物做某事
     *
     * @param item      事物对象
     * @param action    操作
     * @param attribute 附加参数 ["key1", object1, "key2", object2]
     * @return 返回操作结果
     * @throws TryToDoException
     */
    public TryToDoResult tryToDo(long playerID, Action action, Object... attributes);

    /**
     * 尝试让某事物做某事
     *
     * @param item      事物对象
     * @param action    操作
     * @param award     是否创建奖励
     * @param attribute 附加参数 ["key1", object1, "key2", object2]
     * @return 返回操作结果
     * @throws TryToDoException
     */
    public TryToDoResult tryToDo(long playerID, Action action, boolean award, Object... attributes);

    /**
     * 获取操作结果
     *
     * @param item
     * @param action
     * @param attributes
     * @return
     */
    public ActionResult getActionResult(Item<?> item, Action action, Object... attributes);

    /**
     * 获取错做结果
     *
     * @param playerID
     * @param action
     * @param attributes
     * @return
     */
    public ActionResult getActionResult(long playerID, Action action, Object... attributes);

    /**
     * 获取事物对象某操作的条件结果集
     *
     * @param item      事物对象
     * @param action    要执行的操作
     * @param attribute 附加参数 ["key1", object1, "key2", object2]
     * @return 返回该操作的条件结果
     */
    public BehaviorResult getBehaviorResult(Item<?> item, Behavior behavior, Object... attributes);

    /**
     * 获取事物对象某操作的条件结果集
     *
     * @param item      事物对象
     * @param action    要执行的操作
     * @param attribute 附加参数 ["key1", object1, "key2", object2]
     * @return 返回该操作的条件结果
     */
    public BehaviorResult getBehaviorResult(long playerID, Behavior behavior, Object... attributes);

    /**
     * 获取奖励列表
     *
     * @param item
     * @param action
     * @param attributes
     * @return
     */
    public AwardList getAwardList(Item<?> item, Action action, Object... attributes);

    /**
     * 获取奖励列表
     *
     * @param playerID
     * @param action
     * @param attributes
     * @return
     */
    public AwardList getAwardList(long playerID, Action action, Object... attributes);

    /**
     * 获取奖励列表
     *
     * @param item
     * @param action
     * @param attributes
     * @return
     */
    public CostList getCostList(Item<?> item, Action action, Object... attributes);

    /**
     * 获取奖励列表
     *
     * @param playerID
     * @param action
     * @param attributes
     * @return
     */
    public CostList getCostList(long playerID, Action action, Object... attributes);

    /**
     * 计算对该事物进行某操作对象操作的消费
     *
     * @param action    要执行的操作
     * @param attribute 附加参数 ["key1", object1, "key2", object2]
     * @return 返回获得
     */
    public Trade createCostTrade(Item<?> item, Action action, Object... attributes);

    /**
     * 计算对该事物进行某操作对象操作的奖励
     *
     * @param action    要执行的操作
     * @param attribute 附加参数 ["key1", object1, "key2", object2]
     * @return 返回获得
     */
    public Trade createAwardTrade(Item<?> item, Action action, Object... attributes);

    /**
     * 计算对该事物进行某操作对象操作的消费
     *
     * @param action    要执行的操作
     * @param attribute 附加参数 ["key1", object1, "key2", object2]
     * @return 返回获得
     */
    public Trade createCostTrade(long playerID, Action action, Object... attributes);

    /**
     * 计算对该事物进行某操作对象操作的奖励
     *
     * @param action    要执行的操作
     * @param attribute 附加参数 ["key1", object1, "key2", object2]
     * @return 返回获得
     */
    public Trade createAwardTrade(long playerID, Action action, Object... attributes);

    /**
     * 计算某事物某操作的选项
     *
     * @param item      事物对象
     * @param action    要执行的操作
     * @param option    选项类型
     * @param attribute 附加参数 ["key1", object1, "key2", object2]
     * @return 返回能力值
     */
    public <O> O getActionOption(long playerID, Action action, Option option, Object... attributes);

    /**
     * 计算某事物某操作的选项
     *
     * @param item      事物对象
     * @param action    要执行的操作
     * @param option    选项类型
     * @param attribute 附加参数 ["key1", object1, "key2", object2]
     * @return 返回能力值
     */
    public <O> O getActionOption(long playerID, O defaultNum, Action action, Option option, Object... attributes);

    /**
     * 计算某事物某操作的选项
     *
     * @param item      事物对象
     * @param action    要执行的操作
     * @param option    选项类型
     * @param attribute 附加参数 ["key1", object1, "key2", object2]
     * @return 返回能力值
     */
    public <O> O getActionOption(Item<?> item, Action action, Option option, Object... attributes);

    /**
     * 计算某事物某操作的选项
     *
     * @param item      事物对象
     * @param action    要执行的操作
     * @param option    选项类型
     * @param attribute 附加参数 ["key1", object1, "key2", object2]
     * @return 返回能力值
     */
    public <O> O getActionOption(Item<?> item, O defaultNum, Action action, Option option, Object... attributes);

    /**
     * 计算某事物指定能力类型的能力值
     *
     * @param item      事物对象
     * @param ability   能力值类型
     * @param attribute 附加参数 ["key1", object1, "key2", object2]
     * @return 返回能力值
     */
    public <A> A getAbility(long playerID, Ability ability, Object... attributes);

    /**
     * 计算某事物指定能力类型的能力值
     *
     * @param item      事物对象
     * @param ability   能力值类型
     * @param attribute 附加参数 ["key1", object1, "key2", object2]
     * @return 返回能力值
     */
    public <A> A getAbility(long playerID, A defaultObject, Ability ability, Object... attributes);

    /**
     * 计算某事物指定能力类型的能力值
     *
     * @param item      事物对象
     * @param ability   能力值类型
     * @param attribute 附加参数 ["key1", object1, "key2", object2]
     * @return 返回能力值
     */
    public <A> A getAbility(Item<?> item, Ability ability, Object... attributes);

    /**
     * 计算某事物指定能力类型的能力值
     *
     * @param item      事物对象
     * @param ability   能力值类型
     * @param attribute 附加参数 ["key1", object1, "key2", object2]
     * @return 返回能力值
     */
    public <A> A getAbility(Item<?> item, A defaultObject, Ability ability, Object... attributes);

    /**
     * 获取指定ability列表的的能力值
     *
     * @param item
     * @param abilityCollection
     * @param attributes
     * @return
     */
    public <A> Map<Ability, A> getAblilitys(Item<?> item, Collection<Ability> abilityCollection, Object... attributes);

    /**
     * 获取指定ability列表的的能力值
     *
     * @param item
     * @param abilityCollection
     * @param attributes
     * @return
     */
    public <A> Map<Ability, A> getAblilitys(long playerID, Collection<Ability> abilityCollection, Object... attributes);

    /**
     * 获取指定类型的能力值
     *
     * @param item
     * @param abilityClass
     * @param attributes
     * @return
     */
    public <A> Map<Ability, A> getAblilityByType(Item<?> item, Class<? extends Ability> abilityClass, Object... attributes);

    /**
     * 获取指定类型的能力值
     *
     * @param item
     * @param abilityClass
     * @param attributes
     * @return
     */
    public <A> Map<Ability, A> getAblilityByType(long playerID, Class<? extends Ability> abilityClass, Object... attributes);

    public boolean hasAblility(Ability ability);

    public Set<Ability> getOwnAbilityBy(@SuppressWarnings("unchecked") Class<? extends Ability>... abilityClass);

    public boolean hasBehavior(Behavior behavior);

    public boolean hasAction(Action action);

    public Behavior getBehaviorByAction(Action action);

    public boolean hasOption(Action action, Option option);

    public String getItemName();

    public FormulaHolder currentFormula();

}
