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
