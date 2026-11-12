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
