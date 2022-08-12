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

import java.util.Optional;

/**
 * Created by Kun Yang on 16/9/4.
 */
public class ActionTrades {

    private Action action;

    private Trade awardTrade;

    private Trade costTrade;

    public ActionTrades(Action action) {
        this.action = action;
    }

    public ActionTrades(Action action, Trade awardTrade, Trade costTrade) {
        this.action = action;
        if (awardTrade != null && !awardTrade.isEmpty()) {
            this.awardTrade = awardTrade;
        }
        if (costTrade != null && !costTrade.isEmpty()) {
            this.costTrade = costTrade;
        }
    }

    public Action getAction() {
        return action;
    }

    public Optional<Trade> getAwardTrade() {
        return Optional.ofNullable(awardTrade);
    }

    public Optional<Trade> getCostTrade() {
        return Optional.ofNullable(costTrade);
    }

}
