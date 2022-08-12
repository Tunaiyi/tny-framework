/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.expr.mvel;

import com.tny.game.expr.*;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Kun Yang on 2018/6/4.
 */
public class MvelExpressionHolderFactory extends MvelExprHolderFactory {

    public MvelExpressionHolderFactory() {
    }

    public MvelExpressionHolderFactory(boolean oneLine) {
        super(oneLine);
    }

    public MvelExpressionHolderFactory(boolean lazy, boolean online) {
        super(lazy, online);
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
        return new MvelExpression(expr, context, lazy);
    }

}
