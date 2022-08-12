/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item;

import com.tny.game.basics.item.behavior.*;
import com.tny.game.common.context.*;

public abstract class BaseMultipleStuff<SM extends MultipleStuffModel, N extends Number> extends BaseItem<SM> implements MultipleStuff<SM, N> {

    protected BaseMultipleStuff() {
    }

    protected BaseMultipleStuff(long playerId, SM model) {
        super(playerId, model);
    }

    @Override
    public boolean isNumberLimit() {
        return this.model.isNumberLimit();
    }

    @Override
    public boolean tryEnough(long costNum) {
        return !AlterType.CHECK.overLowerLimit(this, costNum);
    }

    protected boolean isLack(N costNum, AlterType alterType) {
        return alterType.overLowerLimit(this, costNum);
    }

    protected boolean isExcess(N receiveNum, AlterType alterType) {
        return alterType.overLowerLimit(this, receiveNum);
    }

    protected abstract void deduct(Action action, TradeItem<SM> tradeItem, Attributes attributes);

    protected abstract void reward(Action action, TradeItem<SM> tradeItem, Attributes attributes);

    protected abstract void setNumber(N number);

    protected AlterType getRewardAlterType(TradeItem<SM> item) {
        return item.getAlertType();
    }

    protected AlterType getDeductAlterType(TradeItem<SM> item) {
        return item.getAlertType();
    }

    /**
     * 执行扣除
     *
     * @param alter
     * @param oldNumber
     * @param current
     * @param action
     */
    protected abstract void postDeduct(N alter, N oldNumber, N current, Action action, Attributes attributes);

    /**
     * 执行增加
     *
     * @param alter
     * @param oldNumber
     * @param current
     * @param action
     */
    protected abstract void postReward(N alter, N oldNumber, N current, Action action, Attributes attributes);

}
