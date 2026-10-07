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

import com.tny.game.basics.item.behavior.*;
import com.tny.game.basics.item.loader.*;
import com.tny.game.basics.item.xml.*;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class GameModelManager<M extends Model> extends LoadableModelManager<M> {

    /**
     * 事物对象管理器
     */
    @Autowired
    protected ItemModelContext context;

    @Autowired
    protected ModelLoaderFactory modelLoaderFactory;

    @Override
    protected ItemModelContext context() {
        return this.context;
    }

    protected GameModelManager(Class<? extends M> modelClass, String... paths) {
        super(modelClass, paths);
        ItemTypes.enumerator().allEnumClasses().forEach(this::addEnumClass);
        Abilities.enumerator().allEnumClasses().forEach(this::addEnumClass);
        Actions.enumerator().allEnumClasses().forEach(this::addEnumClass);
        Behaviors.enumerator().allEnumClasses().forEach(this::addEnumClass);
        DemandTypes.enumerator().allEnumClasses().forEach(this::addEnumClass);
        DemandParams.enumerator().allEnumClasses().forEach(this::addEnumClass);
    }

    protected GameModelManager(Class<? extends M> modelClass, Class<? extends Enum<? extends Option>> optionClass,
            String... paths) {
        this(modelClass, paths);
        this.addEnumClass(optionClass);
    }

    protected GameModelManager(Class<? extends M> modelClass,
            ModelLoaderFactory loaderFactory,
            Class<? extends Enum<? extends DemandType>> demandTypeClass,
            Class<? extends Enum<? extends Ability>> abilityClass,
            Class<? extends Enum<? extends Option>> optionClass, String... paths) {
        this(modelClass, paths);
        this.addEnumClass(abilityClass);
        this.addEnumClass(demandTypeClass);
        this.addEnumClass(optionClass);
    }

    protected GameModelManager(Class<? extends M> modelClass,
            Class<? extends Enum<? extends Ability>> abilityClass,
            Class<? extends Enum<? extends Option>> optionClass, String... paths) {
        this(modelClass, paths);
        this.addEnumClass(abilityClass);
        this.addEnumClass(optionClass);
    }

    protected GameModelManager(
            Class<? extends M> modelClass,
            Class<? extends Enum<?>>[] enumClasses,
            String... paths) {
        this(modelClass, paths);
        for (Class<? extends Enum<?>> clazz : enumClasses) {
            this.addEnumClass(clazz);
        }
    }

    @Override
    protected ModelLoaderFactory loaderFactory() {
        return this.modelLoaderFactory;
    }

}