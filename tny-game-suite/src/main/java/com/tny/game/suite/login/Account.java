package com.tny.game.suite.login;

import com.tny.game.base.item.*;
import com.tny.game.common.event.*;
import com.tny.game.common.utils.*;
import com.tny.game.suite.login.event.*;
import com.tny.game.suite.utils.*;
import org.joda.time.*;

public class Account implements Owned {

    private static BindVoidEventBus<AccountListener, Account> ON_CREATE =
            EventBuses.of(AccountListener.class, AccountListener::onCreate);
    private static BindVoidEventBus<AccountListener, Account> ON_ONLINE =
            EventBuses.of(AccountListener.class, AccountListener::onOnline);
    private static BindVoidEventBus<AccountListener, Account> ON_OFFLINE =
            EventBuses.of(AccountListener.class, AccountListener::onOffline);
    private static BindVoidEventBus<AccountListener, Account> ON_CREATE_ROLE =
            EventBuses.of(AccountListener.class, AccountListener::onCreateRole);

    private long uid;

    private String name;

    private int level;

    private Integer createSid;

    private String account;

    private DateTime createAt;

    private LocalDate createDate;

    private volatile DateTime createRoleAt;

    private volatile LocalDate createRoleDate;

    private volatile DateTime onlineTime;

    private volatile DateTime offlineTime;

    private String device;

    private String deviceId;

    private String ip;

    private String pf;

    private int entry;

    private long zone;

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
        this.createSid = ticket.getServer();
        this.device = ticket.getDevice();
        this.deviceId = ticket.getDeviceId();
        this.createAt = Configs.devDateTime(Configs.DEVELOP_AUTH_CREATE_AT, DateTime.now());
        this.createDate = new LocalDate(this.createAt);
    }

    /**
     * 登录的帐号
     *
     * @return
     */
    public String getOpenId() {
        return AccountUtils.account2OpenId(this.account);
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
    public long getPlayerId() {
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
        if (this.offlineTime == null)
            return null;
        return this.offlineTime.getMillis();
    }

    public Long getOnlineAt() {
        if (this.onlineTime == null)
            return null;
        return this.onlineTime.getMillis();
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
        return DateTimeAide.date2Int(this.createAt);
    }

    public Integer getCreateRoleDate() {
        if (this.createRoleAt == null)
            return null;
        return DateTimeAide.date2Int(this.createRoleAt);
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

    public Integer getCreateSid() {
        return this.createSid;
    }

    protected void setCreateSid(Integer createSid) {
        this.createSid = createSid;
    }

    public int getLevel() {
        return this.level;
    }

    protected void setLevel(int level) {
        this.level = level;
    }

    public int getServerId() {
        return this.createSid;
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

    public String getAccountTag() {
        return AccountUtils.account2Tag(this.account);
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

    public long getZone() {
        return this.zone;
    }

    protected void setZone(long zoneID) {
        this.zone = zoneID;
    }

    public String getDevice() {
        return this.device;
    }

    protected void setDevice(String device) {
        this.device = device;
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    protected void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public boolean isCreateRole() {
        return this.createRoleAt != null && this.createRoleDate != null;
    }

    void updateDevice(GameTicket ticket) {
        this.device = ticket.getDevice();
        this.deviceId = ticket.getDeviceId();
    }

    void onCreate() {
        ON_CREATE.notify(this);
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

    void createRole(DateTime dateTime) {
        this.setCreateRole(dateTime);
        ON_CREATE_ROLE.notify(this);
    }

    boolean online(String ip) {
        if (isOnline())
            return false;
        this.ip = ip;
        this.onlineTime = Configs.devDateTime(Configs.DEVELOP_AUTH_ONLINE_AT, DateTime.now());
        ON_ONLINE.notify(this);
        return true;
    }

    boolean offline() {
        if (!isOnline())
            return false;
        this.offlineTime = Configs.devDateTime(Configs.DEVELOP_AUTH_OFFLINE_AT, DateTime.now());
        ON_OFFLINE.notify(this);
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
        return this.offlineTime;
    }

    public DateTime getOnlineTime() {
        return this.onlineTime;
    }

    @Override
    public String toString() {
        return "Account [uid=" + this.uid + ", name=" + this.name + ", createDate=" + this.createDate + ", pf=" + this.pf + "]";
    }

}
