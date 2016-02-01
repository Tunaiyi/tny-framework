package com.tny.game.suite.oplog.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tny.game.oplog.StuffLog;
import com.tny.game.oplog.TradeLog;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public class ReceiveLogDTO implements StuffLog {

    /**
     * id
     */
    @JsonProperty(index = 1)
    private Long rid;

    /**
     * itemID
     */
    @JsonProperty(index = 2)
    private int riid;

    /**
     * old Number
     */

    @JsonProperty(index = 3)
    private int ronum;

    @JsonProperty(index = 4)
    private int rnnum;

    @JsonProperty(index = 5)
    private Integer ralter;

    public ReceiveLogDTO() {
    }

    public ReceiveLogDTO(TradeLog log) {
        this.rid = log.getID() == log.getItemID() ? null : log.getID();
        this.riid = log.getItemID();
        this.ronum = log.getOldNum();
        this.rnnum = log.getNewNum();
        this.ralter = log.getAlter();
    }

    @Override
    public long getID() {
        return this.rid == null ? this.riid : this.rid;
    }

    @Override
    public int getItemID() {
        return this.riid;
    }

    @Override
    public int getOldNum() {
        return this.ronum;
    }

    @Override
    public int getNewNum() {
        return this.rnnum;
    }

    public int getNum() {
        if (this.ralter == null)
            return this.rnnum - this.ronum;
        return this.ralter;
    }

    public Integer getRalter() {
        return ralter;
    }

    public void setRalter(Integer ralter) {
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

    public int getRnnum() {
        return rnnum;
    }

    public void setRnnum(int rnnum) {
        this.rnnum = rnnum;
    }

    public int getRonum() {
        return ronum;
    }

    public void setRonum(int ronum) {
        this.ronum = ronum;
    }
}
