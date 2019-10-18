package com.tny.game.base.item.xml;

import com.tny.game.base.exception.ItemResultCode;
import com.tny.game.base.item.behavior.*;
import com.tny.game.common.result.ResultCode;
import com.tny.game.expr.ExprHolderFactory;

/**
 * xml映射条件对象
 *
 * @author KGTny
 */
public class XMLDemand extends AbstractDemand {

    public enum TradeDemandType implements DemandType {

        COST_DEMAND_GE(1) {
            @Override
            public ResultCode getResultCode() {
                return ItemResultCode.TRY_TO_DO_FAIL;
            }

            @Override
            public boolean isCost() {
                return true;
            }

        },

        RECV_DEMAND(2) {
            @Override
            public ResultCode getResultCode() {
                return ItemResultCode.TRY_TO_DO_FAIL;
            }

            @Override
            public boolean isCost() {
                return true;
            }

        };

        private int id;

        TradeDemandType(int id) {
            this.id = id;
        }

        @Override
        public Integer getId() {
            return this.id;
        }

    }

    public XMLDemand() {
    }

    protected XMLDemand(String itemAlias, String name, DemandType demandType, String current, String expect, String fx, ExprHolderFactory exprHolderFactory) {
        this.itemAlias = itemAlias;
        this.name = name;
        this.demandType = demandType;
        this.current = exprHolderFactory.create(current);
        this.expect = exprHolderFactory.create(expect);
        this.fx = exprHolderFactory.create(fx);
    }

    protected XMLDemand(String itemAlias, String name, String expect, ExprHolderFactory exprHolderFactory) {
        this.itemAlias = itemAlias;
        this.name = name;
        this.expect = exprHolderFactory.create(expect);
    }


}
