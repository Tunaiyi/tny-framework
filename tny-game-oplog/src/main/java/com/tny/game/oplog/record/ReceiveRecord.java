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

package com.tny.game.oplog.record;

import com.fasterxml.jackson.annotation.*;
import com.tny.game.oplog.*;
import org.apache.commons.lang3.builder.ToStringBuilder;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public class ReceiveRecord implements StuffTradeLog {

    /**
     * id
     */
    @JsonProperty(index = 1)
    private Long rid;

    /**
     * itemId
     */
    @JsonProperty(index = 2)
    private int riid;

    /**
     * old Number
     */
    @JsonProperty(index = 3)
    private long ronum;

    /**
     * new Number
     */
    @JsonProperty(index = 4)
    private long rnnum;

    /**
     * alter Number
     */
    @JsonProperty(index = 5)
    private Long ralter;

    public ReceiveRecord() {
    }

    public ReceiveRecord(StuffTradeLog log) {
        this.rid = log.getId() == log.getModelId() ? null : log.getId();
        this.riid = log.getModelId();
        this.ronum = log.getOldNum();
        this.rnnum = log.getNewNum();
        this.ralter = log.getAlterNum();
    }

    @Override
    public long getId() {
        return this.rid == null ? this.riid : this.rid;
    }

    @Override
    public int getModelId() {
        return this.riid;
    }

    @Override
    public long getOldNum() {
        return this.ronum;
    }

    @Override
    public long getNewNum() {
        return this.rnnum;
    }

    @Override
    public long getAlterNum() {
        return this.ralter;
    }

    public long getNum() {
        if (this.ralter == null) {
            return this.rnnum - this.ronum;
        }
        return this.ralter;
    }

    public Long getRalter() {
        return ralter;
    }

    public void setRalter(Long ralter) {
        this.ralter = ralter;
    }

    public Long getRid() {
        return rid;
    }

    public void setRid(Long rid) {
        this.rid = rid;
    }

    public int getRiid() {
        return riid;
    }

    public void setRiid(int riid) {
        this.riid = riid;
    }

    public long getRnnum() {
        return rnnum;
    }

    public void setRnnum(long rnnum) {
        this.rnnum = rnnum;
    }

    public long getRonum() {
        return ronum;
    }

    public void setRonum(long ronum) {
        this.ronum = ronum;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("rid", rid)
                .append("ralter", ralter)
                .append("riid", riid)
                .append("ronum", ronum)
                .append("rnnum", rnnum)
                .toString();
    }

}
