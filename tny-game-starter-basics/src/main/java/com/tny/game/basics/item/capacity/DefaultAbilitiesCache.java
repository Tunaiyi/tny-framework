package com.tny.game.basics.item.capacity;

import com.tny.game.basics.item.*;
import com.tny.game.common.concurrent.collection.*;

import java.util.*;

/**
 * 能力值缓存
 */
public class DefaultAbilitiesCache<I extends ItemModel> implements AbilitiesCache<I> {

    private volatile Map<Ability, Number> abilityMap = new CopyOnWriteMap<>();

    private Item<?> item;

    protected I model;

    protected long playerId;

    public DefaultAbilitiesCache(Item<? extends I> item) {
        super();
        this.item = item;
        this.model = item.getModel();
    }

    public DefaultAbilitiesCache(long playerId, I model) {
        super();
        this.playerId = playerId;
        this.item = null;
        this.model = model;
    }

    public DefaultAbilitiesCache(Item<?> item, I model) {
        super();
        this.item = item;
        this.model = model;
        this.playerId = item.getPlayerId();
    }

    @Override
    public long getPlayerId() {
        return this.playerId;
    }

    @Override
    public I itemModel() {
        return this.model;
    }

    @Override
    public Number get(Ability ability, Object... attributes) {
        return this.count(this.abilityMap, ability, attributes);
    }

    @Override
    public Number get(Ability ability, Number defaultNum, Object... attributes) {
        Number number = this.get(ability, attributes);
        return number == null ? defaultNum : number;
    }

    @Override
    public boolean hasAbility(Ability ability) {
        Number number = this.abilityMap.get(ability);
        if (number != null) {
            return true;
        }
        return this.model.hasAbility(ability);
    }

    @Override
    public <A extends Ability> Map<A, Number> getAll(Class<A> abilityClass, Object... attributes) {
        if (this.item != null) {
            return this.model.getAbilitiesByType(this.item, abilityClass, Number.class, attributes);
        } else {
            return this.model.getAbilitiesByType(this.playerId, abilityClass, Number.class, attributes);
        }
    }

    @Override
    public Set<Ability> getAllAbilityTypes() {
        return this.model.getAbilityTypes();
    }

    @Override
    public <S extends Ability> Set<S> getAbilityTypes(Class<S> abilityClass) {
        return this.model.getAbilityTypes(abilityClass);
    }

    protected synchronized Number count(Map<Ability, Number> abilityMap, Ability ability, Object... attributes) {
        Number number = abilityMap.get(ability);
        if (number != null) {
            return number;
        }
        ItemModel model = this.model;
        if (model.hasAbility(ability)) {
            if (this.item != null) {
                number = model.getAbility(this.item, ability, Number.class, attributes);
            } else {
                number = model.getAbility(this.playerId, ability, Number.class, attributes);
            }
        }
        if (number != null) {
            Number old = abilityMap.put(ability, number);
            return old == null ? number : old;
        }
        return null;
    }

    protected Number calculate(Ability ability, Number number) {
        return number;
    }

    @Override
    public void refresh() {
        this.abilityMap = new CopyOnWriteMap<>();
    }

}
