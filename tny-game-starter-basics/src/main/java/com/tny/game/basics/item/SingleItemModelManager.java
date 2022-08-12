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