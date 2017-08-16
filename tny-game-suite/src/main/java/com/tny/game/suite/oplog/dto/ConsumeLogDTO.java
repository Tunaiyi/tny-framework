package com.tny.game.suite.oplog.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tny.game.oplog.StuffLog;
import com.tny.game.oplog.TradeLog;
import org.apache.commons.lang3.builder.ToStringBuilder;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public class ConsumeLogDTO implements StuffLog {

    /**
     * id
     */
    @JsonProperty(index = 1)
    private Long cid;

    /**
     * itemID
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

    @JsonProperty(index = 5)
    private Long calter;

    public ConsumeLogDTO() {
    }

    public ConsumeLogDTO(TradeLog log) {
        this.cid = log.getID() == log.getItemID() ? null : log.getID();
        this.ciid = log.getItemID();
        this.conum = log.getOldNum();
        this.cnnum = log.getNewNum();
        this.calter = log.getAlter();
    }

    @Override
    public long getID() {
        return this.cid == null ? this.ciid : this.cid;
    }

    @Override
    public int getItemID() {
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
        if (this.calter == null)
            return this.cnnum - this.conum;
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
