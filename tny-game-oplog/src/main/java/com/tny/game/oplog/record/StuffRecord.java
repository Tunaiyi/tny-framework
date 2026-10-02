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
