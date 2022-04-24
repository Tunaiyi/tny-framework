package com.tny.game.basics.item.behavior;

import com.tny.game.basics.item.*;

import java.util.Map;

/**
 * 条件结果
 *
 * @author KGTny
 */
public class CostDemandResult extends DemandResult {

    private AlterType alterType;

    private StuffModel stuffModel;

    public CostDemandResult(long id, StuffModel stuffModel, DemandType demandType, Object currentValue, Object expectValue, boolean satisfy,
            AlterType alterType, Map<DemandParam, Object> paramMap) {
        super(id, stuffModel, demandType, currentValue, expectValue, satisfy, paramMap);
        this.alterType = alterType;
        this.stuffModel = stuffModel;
    }

    public AlterType getAlterType() {
        return alterType;
    }

    public StuffModel getStuffModel() {
        return stuffModel;
    }

}
