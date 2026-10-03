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
import org.apache.commons.lang3.builder.ToStringBuilder;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public class ConsumeRecord implements StuffTradeLog {

    /**
     * id
     */
    @JsonProperty(index = 1)
    private Long cid;

    /**
     * itemId
     */
    @JsonProperty(index = 2)
    private int ciid;

    /**
     * old Number
     */
    @JsonProperty(index = 3)
    private long conum;

    /**
     * new Number
     */
    @JsonProperty(index = 4)
    private long cnnum;

    /**
     * alter Number
     */
    @JsonProperty(index = 5)
    private Long calter;

    public ConsumeRecord() {
    }

    public ConsumeRecord(StuffTradeLog log) {
        this.cid = log.getId() == log.getModelId() ? null : log.getId();
        this.ciid = log.getModelId();
        this.conum = log.getOldNum();
        this.cnnum = log.getNewNum();
        this.calter = log.getAlterNum();
    }

    @Override
    public long getId() {
        return this.cid == null ? this.ciid : this.cid;
    }

    @Override
    public int getModelId() {
        return this.ciid;
    }

    @Override
    public long getOldNum() {
        return this.conum;
    }

    @Override
    public long getNewNum() {
        return this.cnnum;
    }

    @Override
    public long getAlterNum() {
        return calter;
    }

    public long getNum() {
        if (this.calter == null) {
            return this.cnnum - this.conum;
        }
        return this.calter;
    }

    public Long getCalter() {
        return calter;
    }

    public void setCalter(Long calter) {
        this.calter = calter;
    }

    public Long getCid() {
        return cid;
    }

    public void setCid(Long cid) {
        this.cid = cid;
    }

    public int getCiid() {
        return ciid;
    }

    public void setCiid(int ciid) {
        this.ciid = ciid;
    }

    public long getCnnum() {
        return cnnum;
    }

    public void setCnnum(long cnnum) {
        this.cnnum = cnnum;
    }

    public long getConum() {
        return conum;
    }

    public void setConum(long conum) {
        this.conum = conum;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("cid", cid)
                .append("calter", calter)
                .append("ciid", ciid)
                .append("conum", conum)
                .append("cnnum", cnnum)
                .toString();
    }

}
