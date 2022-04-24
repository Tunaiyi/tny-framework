package com.tny.game.suite.cluster.game;

import com.tny.game.common.event.bus.*;
import com.tny.game.suite.cluster.*;
import com.tny.game.suite.cluster.event.*;
import com.tny.game.suite.core.*;
import com.tny.game.zookeeper.*;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.zookeeper.CreateMode;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

public class GameServerCluster extends SpringBaseCluster {

    private final static Logger LOGGER = LoggerFactory.getLogger(GameServerCluster.class);

    private static final String DB_HOST = "jdbc.datasource.host";

    private static final String DB_PORT = "jdbc.datasource.port";

    private static final String DB_NAME = "jdbc.datasource.db";

    private static final BindP1EventBus<GameServerClusterListener, GameServerCluster, ServerSetting> ON_SETTING_CHANGE =
            EventBuses.of(GameServerClusterListener.class, GameServerClusterListener::onChange);

    private volatile ServerSetting serverSetting;

    private final NodeWatcher<ServerSetting> settingHandler = (path, state, oldData, newData) -> {
        switch (state) {
            case CREATE:
                this.serverSetting = newData;
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("path : {} | 服务器 {} Setting 创建监听!", path, this.serverSetting);
                }
                ON_SETTING_CHANGE.notify(this, newData);
                break;
            case CHANGE:
                this.serverSetting = newData;
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("path : {} | 服务器 {} Setting 发生变化!", path, this.serverSetting);
                }
                ON_SETTING_CHANGE.notify(this, newData);
                break;
            case DELETE:
                break;
        }
    };

    public GameServerCluster(String... monitorServerTypes) {
        super(monitorServerTypes);
    }

    @Override
    protected void register() {
        List<ServerOutline> outlines = this.currentConfigure();
        for (ServerOutline outline : outlines) {
            String currentOutlinePath = ClusterUtils.getServerOutlinePath(outline.getServerId());
            String currentLaunchPath = ClusterUtils.getServerLaunchPath(outline.getServerId());
            // 注册outline
            //	if (this.remoteMonitor.getKeeper().exists(this.currentOutlinePath, false) == null)
            this.remoteMonitor.putNodeData(CreateMode.PERSISTENT, outline, currentOutlinePath);
            // 注册launchState
            ServerLaunch launch = new ServerLaunch(outline.getServerId());
            this.remoteMonitor.putNodeData(CreateMode.EPHEMERAL, launch, currentLaunchPath);
        }
    }

    @Override
    protected void monitor() throws Exception {
        super.monitor();
        int serverID = GameInfo.info().getZoneId();
        String currentSettingPath = ClusterUtils.getServerSettingPath(serverID);
        // 注册监听setting
        //	ServerSetting setting = new ServerSetting(outline);
        this.remoteMonitor.createFullNode(currentSettingPath, new byte[0], CreateMode.PERSISTENT, false, ClusterUtils.NO_FORMATTER);
        this.remoteMonitor.monitorNode(currentSettingPath, this.settingHandler);
    }

    public ServerSetting getServerSetting() {
        return this.serverSetting;
    }

    public boolean isOnline() {
        ServerSetting setting = this.getServerSetting();
        return setting != null && setting.getServerState() == ServerState.ONLINE;
    }

    private List<ServerOutline> currentConfigure() {
        List<ServerOutline> outlines = new ArrayList<>();
        GameInfo main = GameInfo.info();
        for (GameInfo info : GameInfo.getAllGamesInfo()) {
            if (!info.isRegister()) {
                continue;
            }
            Collection<InetConnector> publicConnectors = new CopyOnWriteArraySet<>(info.getPublicConnectors());
            Collection<InetConnector> privateConnectors = new CopyOnWriteArraySet<>(info.getPrivateConnectors());
            ServerOutline outline = new ServerOutline()
                    .setServerId(info.getZoneId())
                    .setServerScope(info.getScopeType().getName())
                    .setServerType(info.getScopeType().getAppType().getName())
                    .setMain(info.isMainServer())
                    .setOpenDate(info.getOpenDate())
                    .setVersion(GameInfo.info().getVersion())
                    // .setServerPort(ip.getPorts().get(0))
                    // .setPublicIP(config.getStr(Configs.PUBLIC_HOST))
                    // .setPrivateIP(config.getStr(Configs.PRIVATE_HOST))
                    // .setRmiPort(config.getInt(Configs.RMI_PORT))
                    .setDbHost(System.getProperty(DB_HOST))
                    .setDbPort(NumberUtils.toInt(System.getProperty(DB_PORT, "0")))
                    .setDb(System.getProperty(DB_NAME))
                    .setFollowServer(main.getZoneId())
                    .setPublicConnectors(publicConnectors)
                    .setPrivateConnectors(privateConnectors);
            outlines.add(outline);
        }
        return outlines;
    }

}
