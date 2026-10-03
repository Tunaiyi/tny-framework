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

package com.tny.game.doc.output;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.tny.game.doc.table.*;

import java.io.IOException;

/**
 * Created by Kun Yang on 2017/4/8.
 */
class JSONExporter implements Exporter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    JSONExporter() {
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE)
                .setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);
    }

    @Override
    public String output(OutputScheme table) throws IOException {
        try {
            return objectMapper.writeValueAsString(table);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

}
