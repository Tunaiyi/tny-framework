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

public class ConstNumberExpr extends MapExpr {

    public ConstNumberExpr(Number number) {
        super(number);
    }

    public ConstNumberExpr(ConstNumberExpr expr) {
        super(expr.number);
    }

    @Override
    protected Object execute() throws Exception {
        return number;
    }

    @Override
    public Expr createExpr() {
        return new ConstNumberExpr(this);
    }

}
