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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.tny.game.expr.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.*;

import java.io.IOException;

public class ExprHolderDeserializer extends JsonDeserializer<ExprHolder> {

    private static final Logger LOG = LoggerFactory.getLogger(ExprHolderDeserializer.class);

    private final ExprHolderFactory exprHolderFactory;

    public ExprHolderDeserializer(ExprHolderFactory exprHolderFactory) {
        this.exprHolderFactory = exprHolderFactory;
    }

    @Override
    public ExprHolder deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        String expr = p.getValueAsString();
        try {
            if (StringUtils.isBlank(expr)) {
                return null;
            }
            return exprHolderFactory.create(expr);
        } catch (Throwable e) {
            LOG.error("{}", expr, e);
            throw e;
        }
    }

}