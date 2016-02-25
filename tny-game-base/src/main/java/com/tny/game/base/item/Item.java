package com.tny.game.base.item;

import com.tny.game.base.item.behavior.Action;
import com.tny.game.base.item.behavior.ActionResult;
import com.tny.game.base.item.behavior.AwardList;
import com.tny.game.base.item.behavior.Behavior;
import com.tny.game.base.item.behavior.BehaviorResult;
import com.tny.game.base.item.behavior.CostList;
import com.tny.game.base.item.behavior.Option;
import com.tny.game.base.item.behavior.TryToDoResult;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * 事物对象接口
 *
 * @param <M>
 * @author KGTny
 */
public interface Item<M extends ItemModel> extends Identifiable {

    /**
     * 获取对象ID
     *
     * @return
     */
    long getID();

    /**
     * 获取对象别名
     *
     * @return
     */
    String getAlias();

    /**
     * 获取该事物对象ID
     *
     * @return
     */
    int getItemID();

    /**
     * 获取该事物对象的模型
     *
     * @return
     */
    M getModel();

    /**
     * 获取该事物对象所属类型
     *
     * @return
     */
    <IT extends ItemType> IT getItemType();

    /**
     * 尝试让该事物对象执行某指定操作
     *
     * @param action     操作类型
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 返回操作结果
     */
    TryToDoResult tryToDo(Action action, Object... attributes);

    /**
     * 尝试让该事物对象执行某指定操作
     *
     * @param action     操作类型
     * @param award      是否创建奖励
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 返回操作结果
     */
    TryToDoResult tryToDo(boolean award, Action action, Object... attributes);

    /**
     * 获取指定action的奖励列表
     *
     * @param action
     * @param attributes
     * @return
     */
    AwardList getAwardList(Action action, Object... attributes);

    /**
     * 获取指定action的奖励列表
     *
     * @param action
     * @param attributes
     * @return
     */
    CostList getCostList(Action action, Object... attributes);

    /**
     * 计算对该事物进行某操作对象操作的消费
     *
     * @param action     要执行的操作
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 返回获得
     */
    Trade createCost(Action action, Object... attributes);

    /**
     * 计算对该事物进行某操作对象操作的奖励
     *
     * @param action     要执行的操作
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 返回获得
     */
    Trade createAward(Action action, Object... attributes);

    /**
     * 获对该事物执行某种行为所需要的行为结果，包括条件，消耗奖励物品
     *
     * @param action     要执行的操作
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 返回奖励列表
     */
    BehaviorResult getBehaviorResult(Behavior behavior, Object... attributes);

    boolean hasAblility(Ability ability);

    boolean hasOption(Action action, Option option);

    <A> A getAbility(Ability ability, Object... attributes);

    <A> A getAbility(A defaultObject, Ability ability, Object... attributes);

    <A> Map<Ability, A> getAblilitys(Collection<Ability> abilityCollection, Object... attributes);

    <A> Map<Ability, A> getAblilityByType(Class<? extends Ability> abilityClass, Object... attributes);

    <O> O getActionOption(Action action, Option option, Object... attributes);

    <O> O getActionOption(Action action, O defaultNum, Option option, Object... attributes);

    ActionResult getActionResult(Action action, Object... attributes);

    boolean hasBehavior(Behavior behavior);

    boolean hasAction(Action action);

    @SuppressWarnings("unchecked")
    Set<Ability> getOwnAbilityBy(Class<? extends Ability>... abilityClass);

    Behavior getBehaviorByAction(Action action);

}
