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

package com.tny.game.basics.item.loader.jackson;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.tny.game.basics.item.*;

import java.io.IOException;

import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/17 03:00
 **/
public class ModelManagerDeserializer<IM extends Model> extends JsonDeserializer<IM> {

    private final ModelManager<IM> modelManager;

    public static <IM extends Model> ModelManagerDeserializer<IM> of(ModelManager<IM> modelManager) {
        return new ModelManagerDeserializer<>(modelManager);
    }

    private ModelManagerDeserializer(ModelManager<IM> modelManager) {
        this.modelManager = modelManager;
    }

    public void bind(SimpleModule module) {
        module.addDeserializer(modelManager.getModelClass(), this);
    }

    @Override
    public IM deserialize(JsonParser p, DeserializationContext context) throws IOException {
        JsonToken token = p.getCurrentToken();
        if (token == JsonToken.VALUE_NULL) {
            return null;
        }
        if (token == JsonToken.VALUE_STRING) {
            return modelManager.getModelByAlias(p.getValueAsString(""));
        }
        if (token == JsonToken.VALUE_NUMBER_INT) {
            return modelManager.getModel(p.getValueAsInt(0));
        }
        throw new IllegalArgumentException(format("{} 无法解析值 {}", modelManager, p.getCurrentValue()));
    }

}
