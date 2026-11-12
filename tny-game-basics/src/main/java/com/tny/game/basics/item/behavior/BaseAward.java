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
import com.tny.game.basics.item.behavior.simple.*;

import java.util.Map;

import static com.tny.game.basics.item.ItemsImportKey.*;

/**
 * 奖励对象
 *
 * @author KGTny
 */
public abstract class BaseAward extends DemandParamsObject implements Award {

    public abstract void init();

    @Override
    public TradeItem<StuffModel> createTradeItem(boolean valid, StuffModel awardModel, Map<String, Object> attributeMap) {
        AlterType type = this.getAlterType();
        Map<DemandParam, Object> paramMap = this.countAndSetDemandParams($PARAMS, attributeMap);
        Number number = this.countNumber(awardModel, attributeMap);
        if (number.doubleValue() > 0.0) {
            return new SimpleTradeItem<>(awardModel, number, type == null ? AlterType.IGNORE : type, valid, paramMap);
        } else {
            return null;
        }
    }

}
