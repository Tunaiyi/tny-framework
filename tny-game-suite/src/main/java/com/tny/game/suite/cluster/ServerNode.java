package com.tny.game.suite.cluster;


import com.tny.game.common.utils.DateTimeHelper;
import com.tny.game.event.BindP1EventBus;
import com.tny.game.event.EventBuses;
import com.tny.game.suite.cluster.event.ServerNodeListener;
import com.tny.game.suite.cluster.game.ServerLaunch;
import com.tny.game.suite.cluster.game.ServerOutline;
import com.tny.game.suite.cluster.game.ServerSetting;
import com.tny.game.suite.cluster.game.ServerState;
import org.joda.time.LocalDate;

public class ServerNode {

    private static final BindP1EventBus<ServerNodeListener, ServerNode, ServerLaunch> ON_LAUNCH_CHANGE =
            EventBuses.of(ServerNodeListener.class, ServerNodeListener::onLaunchChange);

    private static final BindP1EventBus<ServerNodeListener, ServerNode, ServerOutline> ON_OUTLINE_CHANGE =
            EventBuses.of(ServerNodeListener.class, ServerNodeListener::onOutlineChange);

    private static final BindP1EventBus<ServerNodeListener, ServerNode, ServerSetting> ON_SETTING_CHANGE =
            EventBuses.of(ServerNodeListener.class, ServerNodeListener::onSettingChange);

    private ServerOutline outline;

    private ServerSetting setting;

    private ServerLaunch launch;

    public ServerNode() {
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

    public int getServerID() {
        return this.outline.getServerID();
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
        if (outline == null)
            return false;
        LocalDate data = outline.getOpenLocalDate();
        LocalDate now = DateTimeHelper.now();
        return !now.isBefore(data);
    }

    @Override
    public String toString() {
        return "ServerNode [outline=" + this.outline + ", setting=" + this.setting + ", launch=" + this.launch + "]";
    }

}