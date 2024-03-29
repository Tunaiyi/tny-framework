/**
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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