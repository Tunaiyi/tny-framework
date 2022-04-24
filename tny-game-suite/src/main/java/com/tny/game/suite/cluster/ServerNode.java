package com.tny.game.suite.cluster;

import com.tny.game.common.event.bus.*;
import com.tny.game.common.utils.*;
import com.tny.game.suite.cluster.event.*;
import com.tny.game.suite.cluster.game.*;
import com.tny.game.suite.core.*;

import java.time.LocalDate;
import java.util.Optional;

public class ServerNode {

    private static final BindP1EventBus<ServerNodeListener, ServerNode, ServerLaunch> ON_LAUNCH_CHANGE =
            EventBuses.of(ServerNodeListener.class, ServerNodeListener::onLaunchChange);

    private static final BindP1EventBus<ServerNodeListener, ServerNode, ServerOutline> ON_OUTLINE_CHANGE =
            EventBuses.of(ServerNodeListener.class, ServerNodeListener::onOutlineChange);

    private static final BindP1EventBus<ServerNodeListener, ServerNode, ServerSetting> ON_SETTING_CHANGE =
            EventBuses.of(ServerNodeListener.class, ServerNodeListener::onSettingChange);

    private int serverID;

    private ServerOutline outline;

    private ServerSetting setting;

    private ServerLaunch launch;

    public ServerNode(int serverID) {
        this.serverID = serverID;
    }

    public boolean isWork() {
        return this.setting != null && this.setting.getServerState() != ServerState.INVALID;
    }

    public boolean isEmpty() {
        return this.outline == null || this.setting == null;
    }

    public boolean isHasOutline() {
        return this.outline != null;
    }

    public int getServerId() {
        return this.serverID;
    }

    public ServerOutline getOutline() {
        return this.outline;
    }

    public ServerSetting getSetting() {
        return this.setting;
    }

    public ServerLaunch getLaunch() {
        return this.launch;
    }

    public boolean isLaunch() {
        return this.launch != null;
    }

    protected void setOutline(ServerOutline outline) {
        ServerOutline old = this.outline;
        this.outline = outline;
        ON_OUTLINE_CHANGE.notify(this, old);
    }

    protected void setSetting(ServerSetting setting) {
        ServerSetting old = this.setting;
        this.setting = setting;
        ON_SETTING_CHANGE.notify(this, old);
    }

    protected void setLaunch(ServerLaunch launch) {
        ServerLaunch old = this.launch;
        this.launch = launch;
        ON_LAUNCH_CHANGE.notify(this, old);
    }

    public boolean isInOpenDate() {
        ServerOutline outline = this.outline;
        if (outline == null) {
            return false;
        }
        LocalDate data = outline.getOpenLocalDate();
        LocalDate now = DateTimeAide.today();
        return !now.isBefore(data);
    }

    public Optional<InetConnector> getPublicConnector(String... ids) {
        ServerOutline outline = this.outline;
        if (outline == null) {
            return null;
        }
        return Optional.ofNullable(outline.getPublicConnector(ids));
    }

    public Optional<InetConnector> getPrivateConnector(String... ids) {
        ServerOutline outline = this.outline;
        if (outline == null) {
            return null;
        }
        return Optional.ofNullable(outline.getPrivateConnector(ids));
    }

    @Override
    public String toString() {
        return "ServerNode [outline=" + this.outline + ", setting=" + this.setting + ", launch=" + this.launch + "]";
    }

}
