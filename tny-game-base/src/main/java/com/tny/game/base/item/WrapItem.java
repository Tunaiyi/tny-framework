package com.tny.game.base.item;

import com.tny.game.base.item.behavior.*;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public abstract class WrapItem<IM extends ItemModel> extends AbstractItem<IM> implements Item<IM> {

    private Item<IM> item;

    @SuppressWarnings("unchecked")
    protected WrapItem(Item<? extends IM> item) {
        this.item = (Item<IM>) item;
        this.playerID = item.getPlayerID();
        this.model = item.getModel();
    }

    @Override
    public long getID() {
        return this.item.getID();
    }

    @Override
    public String getAlias() {
        return this.item.getAlias();
    }

    @Override
    public int getItemID() {
        return this.item.getItemID();
    }

    @Override
    public long getPlayerID() {
        return this.item.getPlayerID();
    }

    @Override
    public IM getModel() {
        return this.item.getModel();
    }

    @Override
    public <IT extends ItemType> IT getItemType() {
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
    public boolean hasAblility(Ability ability) {
        return this.item.hasAblility(ability);
    }

    @Override
    public boolean hasOption(Action action, Option option) {
        return this.item.hasOption(action, option);
    }

    @Override
    public <A> A getAbility(Ability ability, Object... attributes) {
        return this.item.getAbility(ability, attributes);
    }

    @Override
    public <A> A getAbility(A defaultObject, Ability ability, Object... attributes) {
        return this.item.getAbility(defaultObject, ability, attributes);
    }

    @Override
    public <A> Map<Ability, A> getAblilitys(Collection<Ability> abilityCollection, Object... attributes) {
        return this.item.getAblilitys(abilityCollection, attributes);
    }

    @Override
    public <A> Map<Ability, A> getAblilityByType(Class<? extends Ability> abilityClass, Object... attributes) {
        return this.item.getAblilityByType(abilityClass, attributes);
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
