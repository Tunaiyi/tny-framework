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
