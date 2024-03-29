/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item;

import com.tny.game.basics.item.behavior.*;

import java.util.*;

public abstract class WrapItem<IM extends ItemModel, I extends Item<? extends IM>> extends BaseItem<IM> implements Item<IM> {

    protected I item;

    @SuppressWarnings("unchecked")
    protected WrapItem(I item) {
        this.item = item;
        this.playerId = item.getPlayerId();
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
    public int getModelId() {
        return this.item.getModelId();
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
