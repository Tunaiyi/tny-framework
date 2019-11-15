package com.tny.game.base.item.behavior;

import com.tny.game.base.item.*;

import java.util.Map;

/**
 * 条件结果
 *
 * @author KGTny
 */
public class CostDemandResult extends DemandResult {

    private AlterType alterType;

    public CostDemandResult(long id, ItemModel itemModel, DemandType demandType, Object currentValue, Object expectValue, boolean satisfy,
            AlterType alterType, Map<DemandParam, Object> paramMap) {
        super(id, itemModel, demandType, currentValue, expectValue, satisfy, paramMap);
        this.alterType = alterType;
    }

    public AlterType getAlterType() {
        return alterType;
    }
}
