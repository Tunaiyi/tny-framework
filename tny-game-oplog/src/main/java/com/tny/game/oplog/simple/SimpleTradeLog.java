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
