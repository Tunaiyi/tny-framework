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

package com.tny.game.basics.item.loader.xstream;

import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.tny.game.basics.item.xml.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/17 03:00
 **/
public class ModelManagerValueConverter implements SingleValueConverter {

    private final LoadableModelManager<?> modelManager;

    public static ModelManagerValueConverter of(LoadableModelManager<?> modelManager) {
        return new ModelManagerValueConverter(modelManager);
    }

    private ModelManagerValueConverter(LoadableModelManager<?> modelManager) {
        this.modelManager = modelManager;
    }

    @Override
    public String toString(Object obj) {
        return obj == null ? null : obj.toString();
    }

    @Override
    public Object fromString(String str) {
        return modelManager.getAndCheckModelByAlias(str);
    }

    @Override
    public boolean canConvert(Class type) {
        return modelManager.getModelClass().isAssignableFrom(type);
    }

}
