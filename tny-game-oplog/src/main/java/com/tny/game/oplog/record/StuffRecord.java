/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.oplog.record;

import com.fasterxml.jackson.annotation.*;
import com.tny.game.oplog.*;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public class StuffRecord extends StuffSettleLog {

    @JsonProperty(index = 1)
    private int iid;

    @JsonProperty(index = 2)
    private long num;

    @JsonProperty(index = 3)
    private long rnum;

    @JsonProperty(index = 4)
    private long cnum;

    public StuffRecord() {
    }

    public StuffRecord(StuffSettleLog settleLog) {
        this.iid = settleLog.getItemId();
        this.num = settleLog.getNumber();
        this.rnum = settleLog.getReceiveNum();
        this.cnum = settleLog.getConsumeNum();
    }

    public StuffRecord(int iid) {
        this.iid = iid;
    }

    @Override
    public int getItemId() {
        return iid;
    }

    @Override
    public long getNumber() {
        return num;
    }

    @Override
    public long getReceiveNum() {
        return rnum;
    }

    @Override
    public long getConsumeNum() {
        return cnum;
    }

    @Override
    protected void receive(long number, long alter) {
        this.num = number;
        this.rnum += alter;
    }

    @Override
    protected void consume(long number, long alter) {
        this.num = number;
        this.cnum += alter;
    }

}
