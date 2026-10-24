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

package com.tny.game.basics.item.xml;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.tny.game.expr.*;
import org.junit.platform.commons.util.*;
import org.slf4j.*;

import java.io.IOException;

public class ExprHolderDeserialize extends JsonDeserializer<ExprHolder> {

    private static final Logger LOG = LoggerFactory.getLogger(ExprHolderDeserialize.class);

    private final ExprHolderFactory exprHolderFactory;

    public ExprHolderDeserialize(ExprHolderFactory exprHolderFactory) {
        this.exprHolderFactory = exprHolderFactory;
    }

    @Override
    public ExprHolder deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
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