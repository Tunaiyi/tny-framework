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

public abstract class WrapItem<IM extends ItemModel, I extends Item<? extends IM>> extends AbstractItem<IM> implements Item<IM> {

    protected I item;

    @SuppressWarnings("unchecked")
    protected WrapItem(I item) {
        this.item = item;
        this.playerID = item.getPlayerId();
        this.model = item.getModel();
    }

    @Override
    public long getId() {
        return this.item.getId();
    }

    @Override
    public String getAlias() {
        return this.item.getAlias();
    }

    @Override
    public int getItemId() {
        return this.item.getItemId();
    }

    @Override
    public long getPlayerId() {
        return this.item.getPlayerId();
    }

    @Override
    public IM getModel() {
        return this.item.getModel();
    }

    @Override
    public ItemType getItemType() {
        return this.item.getItemType();
    }

    @Override
    public TryToDoResult tryToDo(Action action, Object... attributes) {
        return this.item.tryToDo(action, attributes);
    }

    @Override
    public AwardList getAwardList(Action action, Object... attributes) {
        return this.item.getAwardList(action, attributes);
    }

    @Override
    public CostList getCostList(Action action, Object... attributes) {
        return this.item.getCostList(action, attributes);
    }

    @Override
    public Trade createCost(Action action, Object... attributes) {
        return this.item.createCost(action, attributes);
    }

    @Override
    public Trade createAward(Action action, Object... attributes) {
        return this.item.createAward(action, attributes);
    }

    @Override
    public BehaviorResult getBehaviorResult(Behavior behavior, Object... attributes) {
        return this.item.getBehaviorResult(behavior, attributes);
    }

    @Override
    public boolean hasAbility(Ability ability) {
        return this.item.hasAbility(ability);
    }

    @Override
    public boolean hasOption(Action action, Option option) {
        return this.item.hasOption(action, option);
    }

    @Override
    public <A> Map<Ability, A> getAbilities(Collection<Ability> abilityCollection, Class<A> clazz, Object... attributes) {
        return this.item.getAbilities(abilityCollection, clazz, attributes);
    }

    @Override
    public <A> A getAbility(Ability ability, Class<A> clazz, Object... attributes) {
        return this.item.getAbility(ability, clazz, attributes);
    }

    @Override
    public <A> A getAbility(A defaultObject, Ability ability, Object... attributes) {
        return this.item.getAbility(defaultObject, ability, attributes);
    }

    @Override
    public <A extends Ability, V> Map<A, V> getAbilitiesByType(Class<A> abilityClass, Class<V> clazz, Object... attributes) {
        return this.item.getAbilitiesByType(abilityClass, clazz, attributes);
    }

    @Override
    public <O> O getActionOption(Action action, Option option, Object... attributes) {
        return this.item.getActionOption(action, option, attributes);
    }

    @Override
    public <O> O getActionOption(Action action, O defaultNum, Option option, Object... attributes) {
        return this.item.getActionOption(action, defaultNum, option, attributes);
    }

    @Override
    public ActionResult getActionResult(Action action, Object... attributes) {
        return this.item.getActionResult(action, attributes);
    }

    @Override
    public boolean hasBehavior(Behavior behavior) {
        return this.item.hasBehavior(behavior);
    }

    @Override
    public boolean hasAction(Action action) {
        return this.item.hasAction(action);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<Ability> getOwnAbilityBy(Class<? extends Ability>... abilityClass) {
        return this.item.getOwnAbilityBy(abilityClass);
    }

    @Override
    public Behavior getBehaviorByAction(Action action) {
        return this.item.getBehaviorByAction(action);
    }



}
