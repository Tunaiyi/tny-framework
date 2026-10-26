/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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