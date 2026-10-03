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

package com.tny.game.basics.item.mapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.tny.game.basics.item.*;

import java.io.IOException;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/19 1:20 下午
 */

public class ItemModelJsonDeserializer extends JsonDeserializer<ItemModel> {

    private final GameExplorer gameExplorer;

    public ItemModelJsonDeserializer(GameExplorer gameExplorer) {
        this.gameExplorer = gameExplorer;
    }

    @Override
    public ItemModel deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        switch (p.getCurrentToken()) {
            case VALUE_STRING:
            case VALUE_NUMBER_INT:
            case VALUE_NUMBER_FLOAT:
                return gameExplorer.getModel(p.getValueAsInt());
            case VALUE_NULL:
                return null;
        }
        return null;
    }

}
