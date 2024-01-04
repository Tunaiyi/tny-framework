/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.expr;

import java.util.*;

public abstract class MapExpr extends AbstractExpr {

    /**
     * 屬性
     * <p>
     * qualifier="key:java.lang.String java.lang.Object"
     */
    protected Map<String, Object> attribute;

    protected MapExpr(Number number) {
        super(number);
    }

    protected MapExpr(String expression) {
        super(expression);
    }

    protected MapExpr(MapExpr expr) {
        super(expr);
    }

    @Override
    protected Map<String, Object> attribute() {
        if (attribute == null) {
            attribute = new HashMap<>();
        }
        return attribute;
    }

}
