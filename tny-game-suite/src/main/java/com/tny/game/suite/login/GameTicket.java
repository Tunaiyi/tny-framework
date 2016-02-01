package com.tny.game.suite.login;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.annotations.ProtoExField;
import com.tny.game.suite.SuiteProtoID;
import org.apache.commons.lang3.StringUtils;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@ProtoEx(SuiteProtoID.AUTH_$GAMES_TICKET)
public class GameTicket {

    @JsonProperty
    @ProtoExField(1)
    private String pf;

    @JsonProperty
    @ProtoExField(2)
    private String ad;

    @JsonProperty
    @ProtoExField(3)
    private int server;

    @JsonProperty
    @ProtoExField(4)
    private int zone;

    @JsonProperty
    @ProtoExField(5)
    private int entry;

    @JsonProperty
    @ProtoExField(6)
    private String device;

    @JsonProperty
    @ProtoExField(7)
    private String openID;

    @JsonProperty
    @ProtoExField(8)
    private String openKey;

    @JsonProperty
    @ProtoExField(9)
    private long time;

    @JsonProperty
    @ProtoExField(10)
    private String pwdKey;

    @JsonProperty
    @ProtoExField(11)
    private String ticket;

    @JsonProperty
    @ProtoExField(12)
    private long tokenID;

    @JsonProperty
    @ProtoExField(13)
    private String pfAccount;

    @JsonProperty
    @ProtoExField(14)
    private boolean interior = false;

    @JsonProperty
    @ProtoExField(15)
    private String deviceID;

    public GameTicket() {
        super();
    }

    public GameTicket(long tokenID, int server, String pfAccount, String openID, String openKey,
                      String pf, int zone, int entry, String device, String deviceID, long time, TicketMaker<GameTicket> maker) {
        super();
        this.tokenID = tokenID;
        this.pf = pf;
        this.zone = zone;
        this.entry = entry;
        this.device = StringUtils.isBlank(deviceID) ? "NONE" : device;
        this.deviceID = StringUtils.isBlank(deviceID) ? openID : deviceID;
        this.server = server;
        this.time = time;
        this.openID = openID;
        this.openKey = openKey;
        this.pfAccount = pfAccount;
        this.interior = false;
        if (maker != null)
            this.ticket = maker.make(this);
    }

    public String getPf() {
        return this.pf;
    }

    public String getAd() {
        return this.ad;
    }

    public int getServer() {
        return this.server;
    }

    public int getZone() {
        return this.zone;
    }

    public int getEntry() {
        return this.entry;
    }

    public String getDevice() {
        return this.device;
    }

    public String getDeviceID() {
        return this.deviceID;
    }

    public String getOpenID() {
        return this.openID;
    }

    public String getOpenKey() {
        return this.openKey;
    }

    public long getTime() {
        return this.time;
    }

    public String getPwdKey() {
        return this.pwdKey;
    }

    public String getTicket() {
        return this.ticket;
    }

    public long getTokenID() {
        return this.tokenID;
    }

    public String getPfAccount() {
        return this.pfAccount;
    }

    public boolean isInterior() {
        return this.interior;
    }

    protected void setPf(String pf) {
        this.pf = pf;
    }

    protected void setAd(String ad) {
        this.ad = ad;
    }

    protected void setServer(int server) {
        this.server = server;
    }

    protected void setZone(int zone) {
        this.zone = zone;
    }

    protected void setEntry(int entry) {
        this.entry = entry;
    }

    protected void setDevice(String device) {
        this.device = device;
    }

    protected void setOpenID(String openID) {
        this.openID = openID;
    }

    protected void setOpenKey(String openKey) {
        this.openKey = openKey;
    }

    protected void setTime(long time) {
        this.time = time;
    }

    protected void setPwdKey(String pwdKey) {
        this.pwdKey = pwdKey;
    }

    protected void setTicket(String ticket) {
        this.ticket = ticket;
    }

    protected void setTokenID(long tokenID) {
        this.tokenID = tokenID;
    }

    protected void setPfAccount(String pfAccount) {
        this.pfAccount = pfAccount;
    }

    protected void setInterior(boolean interior) {
        this.interior = interior;
    }

    @Override
    public String toString() {
        return "GamesTicket [pf=" + this.pf + ", ad=" + this.ad + ", server=" + this.server + ", zone=" + this.zone + ", entry=" + this.entry + ", device=" + this.device + ", openID=" + this.openID
                + ", openKey=" + this.openKey + ", time="
                + this.time + ", pwdKey=" + this.pwdKey + ", ticket=" + this.ticket + "]";
    }

}
