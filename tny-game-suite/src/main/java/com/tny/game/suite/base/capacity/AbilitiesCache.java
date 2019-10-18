package com.tny.game.suite.base.capacity;

import com.tny.game.base.item.Ability;
import com.tny.game.base.item.ItemModel;

import java.util.Map;
import java.util.Set;

/**
 * 能力值缓存
 */
public interface AbilitiesCache<I extends ItemModel> {

    long getPlayerId();

    I itemModel();

    Number get(Ability ability, Object... attributes);

    Number get(Ability ability, Number defaultNum, Object... attributes);

    <A extends Ability> Map<A, Number> getAll(Class<A> abilityClass, Object... attributes);

    boolean hasAbility(Ability ability);

    Set<Ability> getAllAbilityTypes();

    <S extends Ability> Set<S> getAbilityTypes(Class<S> abilityClass);

    void refresh();

}
