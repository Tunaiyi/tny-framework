package com.tny.game.base.item.xml;

import com.tny.game.base.exception.ItemResultCode;
import com.tny.game.base.item.*;
import com.tny.game.base.item.behavior.*;
import com.tny.game.common.result.ResultCode;
import com.tny.game.expr.FormulaType;
import com.tny.game.expr.mvel.MvelFormulaFactory;

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
        public Integer getID() {
            return this.id;
        }

    }

    public XMLDemand() {
    }

    protected XMLDemand(String itemAlias, String name, DemandType demandType, String current, String expect, String fx) {
        this.itemAlias = itemAlias;
        this.name = name;
        this.demandType = demandType;
        this.expect = MvelFormulaFactory.create(expect, FormulaType.EXPRESSION);
        this.fx = MvelFormulaFactory.create(fx, FormulaType.EXPRESSION);
    }

    protected XMLDemand(String itemAlias, String name, String fx) {
        this.itemAlias = itemAlias;
        this.name = name;
        this.expect = MvelFormulaFactory.create(fx, FormulaType.EXPRESSION);
    }

    @Override
    public void init(ItemModel itemModel, ItemExplorer itemExplorer, ModelExplorer itemModelExplorer) {
        super.init(itemModel, itemExplorer, itemModelExplorer);
        if (this.itemAlias == null) {
            this.itemAlias = itemModel.getAlias();
        }

        if (this.demandType == null)
            this.demandType = TradeDemandType.COST_DEMAND_GE;

        if (this.itemAlias == null) {
            if (this.itemAliasFx == null)
                AliasCollectUtils.addAlias(null);
        } else {
            AliasCollectUtils.addAlias(this.itemAlias);
        }
        // if (this.demandType.isCost()) {
        //     if (this.fx == null)
        //         this.fx = MvelFormulaFactory.create(DEMAND_FORMULA, FormulaType.EXPRESSION);
        // }
    }

    @Override
    public String toString() {
        return "XMLDemand [demandType=" + this.demandType + ", expect=" + this.expect + "]";
    }

}
