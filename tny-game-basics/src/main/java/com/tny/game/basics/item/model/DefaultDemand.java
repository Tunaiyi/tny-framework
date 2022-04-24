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
