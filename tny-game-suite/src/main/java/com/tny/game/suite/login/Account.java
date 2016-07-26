package com.tny.game.suite.login;

import com.tny.game.base.item.Identifiable;
import com.tny.game.common.utils.DateTimeHelper;
import com.tny.game.suite.utils.Configs;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public class Account implements Identifiable {

    private long uid;

    private String name;

    private int level;

    private Integer createSID;

    private String account;

    private DateTime createAt;

    private LocalDate createDate;

    private volatile DateTime createRoleAt;

    private volatile LocalDate createRoleDate;

    protected volatile DateTime onlineTime;

    protected volatile DateTime offlineTime;

    private String device;

    private String deviceID;

    private String ip;

    private String pf;

    private int entry;

    private int zone;

    public Account() {
        super();
    }

    public Account(long uid, String account, GameTicket ticket) {
        super();
        this.uid = uid;
        this.account = account;
        this.pf = ticket.getPf();
        this.zone = ticket.getZone();
        this.entry = ticket.getEntry();
        this.createSID = ticket.getServer();
        this.device = ticket.getDevice();
        this.deviceID = ticket.getDeviceID();
        this.createAt = Configs.devDateTime(Configs.DEVELOP_AUTH_CREATE_AT, DateTime.now());
        this.createDate = new LocalDate(this.createAt);
    }

    /**
     * 登录的帐号
     *
     * @return
     */
    public String getOpenID() {
        return AccountUtils.account2OpenID(this.account);
    }

    /**
     * 登录的游戏帐号(包涵平台和服务器ID)
     *
     * @return
     */
    public String getAccount() {
        return this.account;
    }

    protected void setAccount(String account) {
        this.account = account;
    }

    public String getIp() {
        return this.ip;
    }

    protected void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public long getPlayerID() {
        return this.uid;
    }

    public long getUid() {
        return this.uid;
    }

    protected void setUid(long uid) {
        this.uid = uid;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCreateAt() {
        return this.createAt.getMillis();
    }

    protected void setCreateAt(Long createAt) {
        if (createAt != null) {
            this.setCreate(new DateTime(createAt));
        }
    }

    public Long getOfflineAt() {
        if (offlineTime == null)
            return null;
        return offlineTime.getMillis();
    }

    public Long getOnlineAt() {
        if (onlineTime == null)
            return null;
        return onlineTime.getMillis();
    }

    public void setOfflineAt(Long offlineTime) {
        if (offlineTime != null)
            this.offlineTime = new DateTime(offlineTime);
    }

    public void setOnlineAt(Long onlineTime) {
        if (onlineTime != null)
            this.onlineTime = new DateTime(onlineTime);
    }

    public Integer getCreateDate() {
        if (this.createAt == null)
            return null;
        return DateTimeHelper.date2Int(this.createAt);
    }

    public Integer getCreateRoleDate() {
        if (this.createRoleAt == null)
            return null;
        return DateTimeHelper.date2Int(this.createRoleAt);
    }

    public DateTime getCreateDateTime() {
        return this.createAt;
    }

    public DateTime getCreateRoleDateTime() {
        return this.createRoleAt;
    }

    public LocalDate getCreateLocalDate() {
        return this.createDate;
    }

    public LocalDate getCreateRoleLocalDate() {
        return this.createRoleDate;
    }

    public Integer getCreateSID() {
        return this.createSID;
    }

    protected void setCreateSID(Integer createSID) {
        this.createSID = createSID;
    }

    public int getLevel() {
        return this.level;
    }

    protected void setLevel(int level) {
        this.level = level;
    }

    public int getServerID() {
        return this.createSID;
    }

    public Long getCreateRoleAt() {
        DateTime dateTime = this.createRoleAt;
        return dateTime == null ? null : dateTime.getMillis();
    }

    protected void setCreateRoleAt(Long createRoleAt) {
        if (createRoleAt != null) {
            this.setCreateRole(new DateTime(createRoleAt));
        }
    }

    public String getPf() {
        return this.pf;
    }

    protected void setPf(String pf) {
        this.pf = pf;
    }

    public int getEntry() {
        return this.entry;
    }

    protected void setEntry(int entry) {
        this.entry = entry;
    }

    public int getZone() {
        return this.zone;
    }

    protected void setZone(int zoneID) {
        this.zone = zoneID;
    }

    public String getDevice() {
        return this.device;
    }

    protected void setDevice(String device) {
        this.device = device;
    }

    public String getDeviceID() {
        return this.deviceID;
    }

    protected void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public boolean isCreateRole() {
        return this.createRoleAt != null && this.createRoleDate != null;
    }

    void updateDevice(GameTicket ticket) {
        this.device = ticket.getDevice();
        this.deviceID = ticket.getDeviceID();
    }

    void createRole(DateTime dateTime) {
        this.setCreateRole(dateTime);
    }

    protected void setCreate(DateTime dateTime) {
        this.createAt = dateTime;
        this.createDate = new LocalDate(this.createAt);
    }

    protected void setCreateRole(DateTime dateTime) {
        this.level = 1;
        this.createRoleAt = dateTime;
        this.createRoleDate = new LocalDate(this.createRoleAt);
    }

    boolean online(String ip) {
        if (isOnline())
            return false;
        this.ip = ip;
        this.onlineTime = Configs.devDateTime(Configs.DEVELOP_AUTH_ONLINE_AT, DateTime.now());
        return true;
    }

    boolean offline() {
        if (!isOnline())
            return false;
        this.offlineTime = Configs.devDateTime(Configs.DEVELOP_AUTH_OFFLINE_AT, DateTime.now());
        return true;
    }

    public boolean isOnline() {
        if (this.onlineTime == null)
            return false;
        if (this.offlineTime == null)
            return true;
        return this.offlineTime.isBefore(this.onlineTime);
    }

    public DateTime getOfflineTime() {
        return offlineTime;
    }

    public DateTime getOnlineTime() {
        return onlineTime;
    }

    @Override
    public String toString() {
        return "Account [uid=" + this.uid + ", name=" + this.name + ", createDate=" + this.createDate + ", pf=" + this.pf + "]";
    }

}
