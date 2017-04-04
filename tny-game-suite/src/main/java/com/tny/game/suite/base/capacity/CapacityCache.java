package com.tny.game.suite.base.capacity;

import com.tny.game.base.item.Ability;

import java.util.Map;
import java.util.Set;

/**
 * 能力值缓存
 */
public abstract class CapacityCache {

    public abstract long getPlayerID();

    public abstract Number get(Ability ability, Object... attributes);

    public abstract Number get(Ability ability, Number defaultNum, Object... attributes);

   public abstract <A extends Ability> Map<A, Number> getAll(Class<A> abilityClass, Object... attributes);

    public abstract boolean hasAbility(Ability ability);

    public abstract Set<Ability> getAllAbilityTypes();

    public abstract <S extends Ability> Set<S> getAbilityTypes(Class<S> abilityClass);

    protected abstract void refresh();

}
