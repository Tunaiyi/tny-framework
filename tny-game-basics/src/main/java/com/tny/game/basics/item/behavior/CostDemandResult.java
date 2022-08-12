/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

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
