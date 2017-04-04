package com.tny.game.suite.base.capacity;

import com.tny.game.base.item.Ability;
import com.tny.game.base.item.Item;
import com.tny.game.base.item.ItemModel;
import com.tny.game.common.utils.collection.CopyOnWriteMap;

import java.util.Map;
import java.util.Set;

/**
 * 能力值缓存
 */
public class DefaultCapacityCache extends CapacityCache {

    private volatile Map<Ability, Number> abilityMap = new CopyOnWriteMap<>();

    private Item<?> item;

    protected ItemModel model;

    protected long playerID;

    public DefaultCapacityCache(Item<?> item) {
        super();
        this.item = item;
        this.model = item.getModel();
    }

    public DefaultCapacityCache(int playerID, ItemModel model) {
        super();
        this.playerID = playerID;
        this.item = null;
        this.model = model;
    }

    public DefaultCapacityCache(Item<?> item, ItemModel model) {
        super();
        this.item = item;
        this.model = model;
        this.playerID = item.getPlayerID();
    }

    @Override
    public long getPlayerID() {
        return this.playerID;
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
        Number number = abilityMap.get(ability);
        if (number != null)
            return true;
        return getModel().hasAbility(ability);
    }

    @Override
    public <A extends Ability> Map<A, Number> getAll(Class<A> abilityClass, Object... attributes) {
        if (this.item != null) {
            return getModel().getAbilitiesByType(this.item, abilityClass, Number.class, attributes);
        } else {
            return getModel().getAbilitiesByType(this.playerID, abilityClass, Number.class, attributes);
        }
    }

    @Override
    public Set<Ability> getAllAbilityTypes() {
        return getModel().getAbilityTypes();
    }

    @Override
    public <S extends Ability> Set<S> getAbilityTypes(Class<S> abilityClass) {
        return getModel().getAbilityTypes(abilityClass);
    }

    protected synchronized Number count(Map<Ability, Number> abilityMap, Ability ability, Object... attributes) {
        Number number = abilityMap.get(ability);
        if (number != null)
            return number;
        ItemModel model = getModel();
        if (model.hasAbility(ability)) {
            if (this.item != null) {
                number = model.getAbility(this.item, ability, Number.class, attributes);
            } else {
                number = model.getAbility(this.playerID, ability, Number.class, attributes);
            }
        }
        if (number != null) {
            Number old = abilityMap.put(ability, number);
            return (old == null ? number : old).intValue();
        }
        return null;
    }

    protected Number calculate(Ability ability, Number number) {
        return number;
    }

    private ItemModel getModel() {
        if (this.model != null)
            return this.model;
        else if (this.item != null)
            this.model = this.item.getModel();
        return this.model;
    }

    @Override
    protected void refresh() {
        this.abilityMap = new CopyOnWriteMap<>();
    }

}
