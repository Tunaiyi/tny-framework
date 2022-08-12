/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item;

import com.google.common.collect.ImmutableSet;
import com.tny.game.basics.item.behavior.*;
import com.tny.game.common.utils.*;

import java.util.Set;
import java.util.stream.Collectors;

public abstract class GameItemModelManager<IM extends ItemModel> extends GameModelManager<IM> implements ItemTypesManager {

    private volatile Set<ItemType> itemTypes = ImmutableSet.of();

    protected GameItemModelManager(Class<? extends IM> modelClass, String... paths) {
        super(modelClass, paths);
    }

    protected GameItemModelManager(Class<? extends IM> modelClass, Class<? extends Enum<? extends Option>> optionClass, String... paths) {
        this(modelClass, paths);
        this.addEnumClass(optionClass);
    }

    protected GameItemModelManager(Class<? extends IM> modelClass,
            Class<? extends Enum<? extends DemandType>> demandTypeClass,
            Class<? extends Enum<? extends Ability>> abilityClass,
            Class<? extends Enum<? extends Option>> optionClass, String... paths) {
        this(modelClass, paths);
        this.addEnumClass(abilityClass);
        this.addEnumClass(demandTypeClass);
        this.addEnumClass(optionClass);
    }

    protected GameItemModelManager(Class<? extends IM> modelClass,
            Class<? extends Enum<? extends Ability>> abilityClass,
            Class<? extends Enum<? extends Option>> optionClass,
            String... paths) {
        this(modelClass, paths);
        this.addEnumClass(abilityClass);
        this.addEnumClass(optionClass);
    }

    protected GameItemModelManager(
            Class<? extends IM> modelClass,
            Class<? extends Enum<?>>[] enumClasses,
            String... paths) {
        this(modelClass, paths);
        for (Class<? extends Enum<?>> clazz : enumClasses) {
            this.addEnumClass(clazz);
        }
    }

    @Override
    protected void parseAllComplete() {
        this.itemTypes = ImmutableSet.copyOf(this.modelMap.values().stream()
                .map(m -> Asserts.checkNotNull(m.getItemType(), "{}.getItemType() is null", m))
                .collect(Collectors.toSet()));
        this.parseAllItemModelComplete();
    }

    protected void parseAllItemModelComplete() {
    }

    @Override
    public Set<ItemType> manageTypes() {
        return this.itemTypes;
    }

}