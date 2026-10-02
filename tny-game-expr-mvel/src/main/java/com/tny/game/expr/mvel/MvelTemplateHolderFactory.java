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

package com.tny.game.expr.mvel;

import com.tny.game.expr.*;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Kun Yang on 2018/6/4.
 */
public class MvelTemplateHolderFactory extends MvelExprHolderFactory {

    public MvelTemplateHolderFactory() {
    }

    public MvelTemplateHolderFactory(boolean oneLine) {
        super(oneLine);
    }

    public MvelTemplateHolderFactory(boolean lazy, boolean oneLine) {
        super(lazy, oneLine);
    }

    @Override
    protected String preProcess(String expression) {
        if (oneLine) {
            return StringUtils.replace(StringUtils.trim(expression), "\n", "");
        } else {
            return expression;
        }
    }

    @Override
    protected ExprHolder createExprHolder(String expr) throws ExprException {
        return new MvelTemplate(expr, context, lazy);
    }

}
