package com.tny.game.basics.item;

import com.tny.game.basics.item.behavior.*;

import java.util.*;

/**
 * 事物对象接口
 *
 * @param <M>
 * @author KGTny
 */
public interface Item<M extends ItemModel> extends Entity<M> {

    /**
     * 获取该事物对象所属类型
     *
     * @return
     */
    ItemType getItemType();

    /**
     * 尝试让该事物对象执行某指定操作,失败立即返回
     *
     * @param action     操作类型
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 返回操作结果
     */
    default TryToDoResult tryToDo(Action action, Object... attributes) {
        return this.getModel().tryToDo(this, action, attributes);
    }

    /**
     * 尝试让该事物对象执行某指定操作,失败立即返回
     *
     * @param action     操作类型
     * @param award      是否创建奖励
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 返回操作结果
     */
    default TryToDoResult tryToDo(boolean award, Action action, Object... attributes) {
        return this.getModel().tryToDo(this, action, award, attributes);
    }

    /**
     * 尝试让该事物对象执行某指定操作,尝试所有条件
     *
     * @param action     操作类型
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 返回操作结果
     */
    default TryToDoResult tryToDoAll(Action action, Object... attributes) {
        return this.getModel().tryToDoAll(this, action, attributes);
    }

    /**
     * 尝试让该事物对象执行某指定操作,尝试所有条件
     *
     * @param action     操作类型
     * @param award      是否创建奖励
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 返回操作结果
     */
    default TryToDoResult tryToDoAll(boolean award, Action action, Object... attributes) {
        return this.getModel().tryToDoAll(this, action, award, attributes);
    }

    /**
     * 获取指定action的奖励列表
     *
     * @param action
     * @param attributes
     * @return
     */
    default AwardList getAwardList(Action action, Object... attributes) {
        return this.getModel().getAwardList(this, action, attributes);
    }

    /**
     * 获取指定action的奖励列表
     *
     * @param action
     * @param attributes
     * @return
     */
    default CostList getCostList(Action action, Object... attributes) {
        return this.getModel().getCostList(this, action, attributes);
    }

    /**
     * 计算对该事物进行某操作对象操作的消费
     *
     * @param action     要执行的操作
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 返回获得
     */
    default Trade createCost(Action action, Object... attributes) {
        return this.getModel().createCostTrade(this, action, attributes);
    }

    /**
     * 计算对该事物进行某操作对象操作的奖励
     *
     * @param action     要执行的操作
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 返回获得
     */
    default Trade createAward(Action action, Object... attributes) {
        return this.getModel().createAwardTrade(this, action, attributes);
    }

    /**
     * 生产奖励&扣除信息
     *
     * @param action     要执行的操作
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 返回获得
     */
    default ActionTrades createActionTrades(Action action, Object... attributes) {
        return this.getModel().createActionTrades(this, action, attributes);
    }

    /**
     * 获对该事物执行某种行为所需要的行为结果，包括条件，消耗奖励物品
     *
     * @param behavior   要执行的操作
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 返回奖励列表
     */
    default BehaviorResult getBehaviorResult(Behavior behavior, Object... attributes) {
        return this.getModel().getBehaviorResult(this, behavior, attributes);
    }

    default boolean hasAbility(Ability ability) {
        return this.getModel().hasAbility(ability);
    }

    default boolean hasBehavior(Behavior behavior) {
        return this.getModel().hasBehavior(behavior);
    }

    default boolean hasAction(Action action) {
        return this.getModel().hasAction(action);
    }

    default boolean hasOption(Action action, Option option) {
        return this.getModel().hasOption(action, option);
    }

    default <A> Map<Ability, A> getAbilities(Collection<Ability> abilityCollection, Class<A> clazz, Object... attributes) {
        return this.getModel().getAbilities(this, abilityCollection, clazz, attributes);
    }

    default <A extends Ability, V> Map<A, V> getAbilitiesByType(Class<A> abilityClass, Class<V> clazz, Object... attributes) {
        return this.getModel().getAbilitiesByType(this, abilityClass, clazz, attributes);
    }

    default <A> A getAbility(Ability ability, Class<A> clazz, Object... attributes) {
        return this.getModel().getAbility(this, ability, clazz, attributes);
    }

    default <A> A getAbility(A defaultObject, Ability ability, Object... attributes) {
        return this.getModel().getAbility(this, defaultObject, ability, attributes);
    }

    default <O> O getActionOption(Action action, Option option, Object... attributes) {
        return this.getModel().getActionOption(this, action, option, attributes);
    }

    default <O> O getActionOption(Action action, O defaultNum, Option option, Object... attributes) {
        return this.getModel().getActionOption(this, defaultNum, action, option, attributes);
    }

    @SuppressWarnings("unchecked")
    default Set<Ability> getOwnAbilityBy(Class<? extends Ability>... abilityClass) {
        return this.getModel().getOwnAbilityBy(abilityClass);
    }

    default ActionResult getActionResult(Action action, Object... attributes) {
        return this.getModel().getActionResult(this, action, attributes);
    }

    default Behavior getBehaviorByAction(Action action) {
        return this.getModel().getBehaviorByAction(action);
    }

}
