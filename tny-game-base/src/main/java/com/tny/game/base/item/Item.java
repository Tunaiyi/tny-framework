package com.tny.game.base.item;

import com.tny.game.base.item.behavior.*;

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
    public long getID();

    /**
     * 获取对象别名
     *
     * @return
     */
    public String getAlias();

    /**
     * 获取该事物对象ID
     *
     * @return
     */
    public int getItemID();

    /**
     * 获取该事物对象的模型
     *
     * @return
     */
    public M getModel();

    /**
     * 获取该事物对象所属类型
     *
     * @return
     */
    public <IT extends ItemType> IT getItemType();

    /**
     * 尝试让该事物对象执行某指定操作
     *
     * @param action     操作类型
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 返回操作结果
     */
    public TryToDoResult tryToDo(Action action, Object... attributes);

    /**
     * 尝试让该事物对象执行某指定操作
     *
     * @param action     操作类型
     * @param award      是否创建奖励
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 返回操作结果
     */
    public TryToDoResult tryToDo(boolean award, Action action, Object... attributes);

    /**
     * 获取指定action的奖励列表
     *
     * @param action
     * @param attributes
     * @return
     */
    public AwardList getAwardList(Action action, Object... attributes);

    /**
     * 获取指定action的奖励列表
     *
     * @param action
     * @param attributes
     * @return
     */
    public CostList getCostList(Action action, Object... attributes);

    /**
     * 计算对该事物进行某操作对象操作的消费
     *
     * @param action     要执行的操作
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 返回获得
     */
    public Trade createCost(Action action, Object... attributes);

    /**
     * 计算对该事物进行某操作对象操作的奖励
     *
     * @param action     要执行的操作
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 返回获得
     */
    public Trade createAward(Action action, Object... attributes);

    /**
     * 获对该事物执行某种行为所需要的行为结果，包括条件，消耗奖励物品
     *
     * @param action     要执行的操作
     * @param attributes 附加参数 ["key1", object1, "key2", object2]
     * @return 返回奖励列表
     */
    public BehaviorResult getBehaviorResult(Behavior behavior, Object... attributes);

    public boolean hasAblility(Ability ability);

    public boolean hasOption(Action action, Option option);

    public <A> A getAbility(Ability ability, Object... attributes);

    public <A> A getAbility(A defaultObject, Ability ability, Object... attributes);

    public <A> Map<Ability, A> getAblilitys(Collection<Ability> abilityCollection, Object... attributes);

    public <A> Map<Ability, A> getAblilityByType(Class<? extends Ability> abilityClass, Object... attributes);

    public <O> O getActionOption(Action action, Option option, Object... attributes);

    public <O> O getActionOption(Action action, O defaultNum, Option option, Object... attributes);

    public ActionResult getActionResult(Action action, Object... attributes);

    public boolean hasBehavior(Behavior behavior);

    public boolean hasAction(Action action);

    @SuppressWarnings("unchecked")
    public Set<Ability> getOwnAbilityBy(Class<? extends Ability>... abilityClass);

    public Behavior getBehaviorByAction(Action action);

}
