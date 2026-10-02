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

package com.tny.game.basics.item;

import com.tny.game.basics.item.behavior.*;
import com.tny.game.common.context.*;

public abstract class LongMultipleStuff<SM extends MultipleStuffModel> extends BaseMultipleStuff<SM, Long> {

    protected long number;

    protected LongMultipleStuff() {
    }

    protected LongMultipleStuff(long playerId, SM model) {
        super(playerId, model);
    }

    @Override
    public Long getNumberLimit() {
        Number number = this.model.countNumberLimit(this);
        if (number == null) {
            return null;
        }
        return number.longValue();
    }

    @Override
    public Long getNumber() {
        return this.number;
    }

    @Override
    protected void deduct(Action action, TradeItem<SM> tradeItem, Attributes attributes) {
        long alter = this.getDeductAlterType(tradeItem).deduct(this, tradeItem.getNumber()).longValue();
        if (alter > 0) {
            long oldNumber = this.getNumber();
            this.number -= alter;
            this.postDeduct(alter, oldNumber, this.number, action, attributes);
        }
    }

    @Override
    protected void reward(Action action, TradeItem<SM> tradeItem, Attributes attributes) {
        long alter = this.getRewardAlterType(tradeItem).reward(this, tradeItem.getNumber()).longValue();
        if (alter > 0) {
            long oldNumber = this.getNumber();
            this.number += alter;
            this.postReward(alter, oldNumber, this.number, action, attributes);
        }
    }

    @Override
    protected void setNumber(Long number) {
        this.number = number;
    }

}
