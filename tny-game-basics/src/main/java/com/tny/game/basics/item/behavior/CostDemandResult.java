/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
