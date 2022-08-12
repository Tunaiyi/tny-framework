/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.oplog.simple;

import com.tny.game.basics.item.*;
import com.tny.game.oplog.*;

public class SimpleTradeLog implements StuffTradeLog {

    private long id;

    private int modelId;

    private long oldNum;

    private long alter;

    private long newNum;

    public SimpleTradeLog(Item<?> item, OpTradeType tradeType, long oldNumber, long alter, long newNumber) {
        super();
        this.id = item.getId();
        this.modelId = item.getModelId();
        this.oldNum = oldNumber;
        this.newNum = newNumber;
        this.alter = tradeType == OpTradeType.CONSUME ? -1 * alter : alter;
    }

    public SimpleTradeLog(long id, int modelId, OpTradeType tradeType, long oldNum, long alter, long newNum) {
        super();
        this.id = id;
        this.modelId = modelId;
        this.oldNum = oldNum;
        this.newNum = newNum;
        this.alter = tradeType == OpTradeType.CONSUME ? -1 * alter : alter;
    }

    public void receive(long alter, long newNum) {
        this.alter += alter;
        this.newNum = newNum;
    }

    public void consume(long alter, long newNum) {
        this.alter -= alter;
        this.newNum = newNum;
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public int getModelId() {
        return this.modelId;
    }

    @Override
    public long getOldNum() {
        return this.oldNum;
    }

    @Override
    public long getNewNum() {
        return this.newNum;
    }

    @Override
    public long getAlterNum() {
        return this.alter;
    }

}
