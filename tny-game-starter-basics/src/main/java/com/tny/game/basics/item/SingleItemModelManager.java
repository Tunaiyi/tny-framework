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

import java.util.List;

import static com.tny.game.common.utils.StringAide.*;

public abstract class SingleItemModelManager<IM extends ItemModel> extends GameItemModelManager<IM> {

    private IM model;

    protected SingleItemModelManager(Class<? extends IM> modelClass, String... paths) {
        super(modelClass, paths);
    }

    protected SingleItemModelManager(Class<? extends IM> modelClass,
            Class<? extends Enum<? extends Option>> optionClass, String... paths) {
        super(modelClass, optionClass, paths);
    }

    protected SingleItemModelManager(Class<? extends IM> modelClass,
            Class<? extends Enum<? extends DemandType>> demandTypeClass,
            Class<? extends Enum<? extends Ability>> abilityClass,
            Class<? extends Enum<? extends Option>> optionClass, String... paths) {
        super(modelClass, demandTypeClass, abilityClass, optionClass, paths);
    }

    protected SingleItemModelManager(Class<? extends IM> modelClass,
            Class<? extends Enum<? extends Ability>> abilityClass,
            Class<? extends Enum<? extends Option>> optionClass, String... paths) {
        super(modelClass, abilityClass, optionClass, paths);
    }

    protected SingleItemModelManager(Class<? extends IM> modelClass, Class<? extends Enum<?>>[] enumClasses, String... paths) {
        super(modelClass, enumClasses, paths);
    }

    @Override
    protected void parseComplete(List<IM> models) {
        if (models.isEmpty()) {
            throw new IllegalArgumentException(format("{} model 列表为空"));
        }
        if (models.size() > 1) {
            throw new IllegalArgumentException(format("{} model 列表数量多于1"));
        }
        model = models.get(0);
        parseComplete(model);
        super.parseComplete(models);
    }

    protected void parseComplete(IM model) {
    }

    public IM getModel() {
        return model;
    }

}