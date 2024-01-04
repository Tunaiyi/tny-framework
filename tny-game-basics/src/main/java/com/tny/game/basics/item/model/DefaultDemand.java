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

package com.tny.game.basics.item.model;

import com.tny.game.basics.item.behavior.*;
import com.tny.game.expr.*;

/**
 * xml映射条件对象
 *
 * @author KGTny
 */
public class DefaultDemand extends BaseDemand {

    public DefaultDemand() {
    }

    public DefaultDemand(String itemAlias, String name, DemandType demandType, String current, String expect, String fx,
            ExprHolderFactory exprHolderFactory) {
        this.itemAlias = itemAlias;
        this.name = name;
        this.demandType = demandType;
        this.current = exprHolderFactory.create(current);
        this.expect = exprHolderFactory.create(expect);
        this.fx = exprHolderFactory.create(fx);
    }

    public DefaultDemand(String itemAlias, String name, String expect, ExprHolderFactory exprHolderFactory) {
        this.itemAlias = itemAlias;
        this.name = name;
        this.expect = exprHolderFactory.create(expect);
    }

}
