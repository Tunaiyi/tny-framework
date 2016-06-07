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
    private long ronum;

    @JsonProperty(index = 4)
    private long rnnum;

    @JsonProperty(index = 5)
    private Long ralter;

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
    public long getOldNum() {
        return this.ronum;
    }

    @Override
    public long getNewNum() {
        return this.rnnum;
    }

    public long getNum() {
        if (this.ralter == null)
            return this.rnnum - this.ronum;
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

    public void setRnnum(int rnnum) {
        this.rnnum = rnnum;
    }

    public long getRonum() {
        return ronum;
    }

    public void setRonum(int ronum) {
        this.ronum = ronum;
    }
}
