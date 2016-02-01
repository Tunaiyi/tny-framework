package com.tny.game.suite.oplog.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tny.game.oplog.StuffLog;
import com.tny.game.oplog.TradeLog;

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
    private int conum;

    /**
     * new Number
     */
    @JsonProperty(index = 4)
    private int cnnum;

    @JsonProperty(index = 5)
    private Integer calter;

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
    public int getOldNum() {
        return this.conum;
    }

    @Override
    public int getNewNum() {
        return this.cnnum;
    }

    public int getNum() {
        if (this.calter == null)
            return this.cnnum - this.conum;
        return this.calter;
    }

    public Integer getCalter() {
        return calter;
    }

    public void setCalter(Integer calter) {
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

    public int getCnnum() {
        return cnnum;
    }

    public void setCnnum(int cnnum) {
        this.cnnum = cnnum;
    }

    public int getConum() {
        return conum;
    }

    public void setConum(int conum) {
        this.conum = conum;
    }
}
