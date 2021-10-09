package com.tny.game.suite.login;

import com.tny.game.basics.item.*;
import com.tny.game.common.event.bus.*;
import com.tny.game.common.utils.*;
import com.tny.game.suite.login.event.*;
import com.tny.game.suite.utils.*;

import java.time.*;

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

    private Instant createAt;

    private LocalDate createDate;

    private volatile Instant createRoleAt;

    private volatile LocalDate createRoleDate;

    private volatile Instant onlineTime;

    private volatile Instant offlineTime;

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
        this.createAt = Configs.devDateTime(Configs.DEVELOP_AUTH_CREATE_AT, Instant.now());
        this.createDate = LocalDate.from(this.createAt);
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

    public long getPlayerId() {
        return this.uid;
    }

    @Override
    public long getOwnerId() {
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
        return this.createAt.toEpochMilli();
    }

    protected void setCreateAt(Long createAt) {
        if (createAt != null) {
            this.setCreate(Instant.ofEpochMilli(createAt));
        }
    }

    public Long getOfflineAt() {
        if (this.offlineTime == null) {
            return null;
        }
        return this.offlineTime.toEpochMilli();
    }

    public Long getOnlineAt() {
        if (this.onlineTime == null) {
            return null;
        }
        return this.onlineTime.toEpochMilli();
    }

    public void setOfflineAt(Long offlineTime) {
        if (offlineTime != null) {
            this.offlineTime = Instant.ofEpochMilli(offlineTime);
        }
    }

    public void setOnlineAt(Long onlineTime) {
        if (onlineTime != null) {
            this.onlineTime = Instant.ofEpochMilli(onlineTime);
        }
    }

    public Integer getCreateDate() {
        if (this.createAt == null) {
            return null;
        }
        return DateTimeAide.date2Int(this.createAt);
    }

    public Integer getCreateRoleDate() {
        if (this.createRoleAt == null) {
            return null;
        }
        return DateTimeAide.date2Int(this.createRoleAt);
    }

    public Instant getCreateDateTime() {
        return this.createAt;
    }

    public Instant getCreateRoleDateTime() {
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
        Instant dateTime = this.createRoleAt;
        return dateTime == null ? null : dateTime.toEpochMilli();
    }

    protected void setCreateRoleAt(Long createRoleAt) {
        if (createRoleAt != null) {
            this.setCreateRole(Instant.ofEpochMilli(createRoleAt));
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

    protected void setCreate(Instant dateTime) {
        this.createAt = dateTime;
        this.createDate = LocalDate.from(this.createAt);
    }

    protected void setCreateRole(Instant dateTime) {
        this.level = 1;
        this.createRoleAt = dateTime;
        this.createRoleDate = LocalDate.from(this.createRoleAt);
    }

    void createRole(Instant dateTime) {
        this.setCreateRole(dateTime);
        ON_CREATE_ROLE.notify(this);
    }

    boolean online(String ip) {
        if (isOnline()) {
            return false;
        }
        this.ip = ip;
        this.onlineTime = Configs.devDateTime(Configs.DEVELOP_AUTH_ONLINE_AT, Instant.now());
        ON_ONLINE.notify(this);
        return true;
    }

    boolean offline() {
        if (!isOnline()) {
            return false;
        }
        this.offlineTime = Configs.devDateTime(Configs.DEVELOP_AUTH_OFFLINE_AT, Instant.now());
        ON_OFFLINE.notify(this);
        return true;
    }

    public boolean isOnline() {
        if (this.onlineTime == null) {
            return false;
        }
        if (this.offlineTime == null) {
            return true;
        }
        return this.offlineTime.isBefore(this.onlineTime);
    }

    public Instant getOfflineTime() {
        return this.offlineTime;
    }

    public Instant getOnlineTime() {
        return this.onlineTime;
    }

    @Override
    public String toString() {
        return "Account [uid=" + this.uid + ", name=" + this.name + ", createDate=" + this.createDate + ", pf=" + this.pf + "]";
    }

}
