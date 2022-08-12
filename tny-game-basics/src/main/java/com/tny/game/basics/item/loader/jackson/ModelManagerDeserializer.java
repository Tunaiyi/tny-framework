/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
