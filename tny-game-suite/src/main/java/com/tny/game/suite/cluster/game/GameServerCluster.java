package com.tny.game.suite.cluster.game;


import com.tny.game.common.event.BindP1EventBus;
import com.tny.game.common.event.EventBuses;
import com.tny.game.common.lifecycle.LifecycleLevel;
import com.tny.game.common.lifecycle.PostStarter;
import com.tny.game.common.lifecycle.ServerPostStart;
import com.tny.game.suite.cluster.BaseCluster;
import com.tny.game.suite.cluster.ClusterUtils;
import com.tny.game.suite.cluster.Servers;
import com.tny.game.suite.cluster.event.GameServerClusterListener;
import com.tny.game.suite.core.GameInfo;
import com.tny.game.suite.core.InetConnector;
import com.tny.game.zookeeper.NodeWatcher;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.tny.game.suite.SuiteProfiles.*;

@Component
@Profile({GAME})
public class GameServerCluster extends BaseCluster implements ServerPostStart {

    private final static Logger LOGGER = LoggerFactory.getLogger(GameServerCluster.class);

    private static final String DB_HOST = "jdbc.datasource.host";
    private static final String DB_PORT = "jdbc.datasource.port";
    private static final String DB_NAME = "jdbc.datasource.db";

    private static final BindP1EventBus<GameServerClusterListener, GameServerCluster, ServerSetting> ON_SETTING_CHANGE =
            EventBuses.of(GameServerClusterListener.class, GameServerClusterListener::onChange);

    private volatile ServerSetting serverSetting;

    private NodeWatcher<ServerSetting> settingHandler = (path, state, oldData, newData) -> {
        switch (state) {
            case CREATE:
                this.serverSetting = newData;
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("path : {} | 服务器 {} Setting 创建监听!", path, this.serverSetting);
                ON_SETTING_CHANGE.notify(this, newData);
                break;
            case CHANGE:
                this.serverSetting = newData;
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("path : {} | 服务器 {} Setting 发生变化!", path, this.serverSetting);
                ON_SETTING_CHANGE.notify(this, newData);
                break;
            case DELETE:
                break;
        }
    };

    public GameServerCluster() {
        super(Servers.GIFT);
    }

    @Override
    protected void doMonitor() {
        this.register();
    }

    private void register() {
        List<ServerOutline> outlines = this.currentConfiger();
        for (ServerOutline outline : outlines) {
            String currentOutlinePath = ClusterUtils.getServerOutlinePath(outline.getServerID());
            String currentLaunchPath = ClusterUtils.getServerLaunchPath(outline.getServerID());
            String currentSettingPath = ClusterUtils.getServerSettingPath(outline.getServerID());
            // 注册outline
            //	if (this.remoteMonitor.getKeeper().exists(this.currentOutlinePath, false) == null)
            this.remoteMonitor.putNodeData(CreateMode.PERSISTENT, outline, currentOutlinePath);
            if (GameInfo.getMainInfo().getServerID() == outline.getServerID()) {
                // 注册监听setting
                //	ServerSetting setting = new ServerSetting(outline);
                this.remoteMonitor.createFullNode(currentSettingPath, new byte[0], CreateMode.PERSISTENT, false, ClusterUtils.NO_FORMATTER);
                this.remoteMonitor.monitorNode(currentSettingPath, this.settingHandler);
            }
            // 注册launchState
            ServerLaunch launch = new ServerLaunch(outline.getServerID());
            this.remoteMonitor.putNodeData(CreateMode.EPHEMERAL, launch, currentLaunchPath);
        }
    }

    public ServerSetting getServerSetting() {
        return this.serverSetting;
    }

    public boolean isOnline() {
        ServerSetting setting = this.getServerSetting();
        return setting != null && setting.getServerState() == ServerState.ONLINE;
    }

    private List<ServerOutline> currentConfiger() {
        List<ServerOutline> outlines = new ArrayList<>();
        GameInfo main = GameInfo.getMainInfo();
        for (GameInfo info : GameInfo.getAllGamesInfo()) {
            if (!info.isRegister())
                continue;
            Collection<InetConnector> publicConnectors = new CopyOnWriteArraySet<>(info.getPublicConnectors());
            Collection<InetConnector> privateConnectors = new CopyOnWriteArraySet<>(info.getPrivateConnectors());
            ServerOutline outline = new ServerOutline()
                    .setServerID(info.getServerID())
                    .setServerScope(info.getScopeType().getName())
                    .setServerType(info.getScopeType().getAppType().getName())
                    .setMain(info.isMainServer())
                    .setOpenDate(info.getOpenDate())
                    .setVersion(GameInfo.getMainInfo().getVersion())
                    // .setServerPort(ip.getPorts().get(0))
                    // .setPublicIP(config.getStr(Configs.PUBLIC_HOST))
                    // .setPrivateIP(config.getStr(Configs.PRIVATE_HOST))
                    // .setRmiPort(config.getInt(Configs.RMI_PORT))
                    .setDbHost(System.getProperty(DB_HOST))
                    .setDbPort(NumberUtils.toInt(System.getProperty(DB_PORT, "0")))
                    .setDb(System.getProperty(DB_NAME))
                    .setFollowServer(main.getServerID())
                    .setPublicConnectors(publicConnectors)
                    .setPrivateConnectors(privateConnectors);
            outlines.add(outline);
        }
        return outlines;
    }

    @Override
    public PostStarter getPostStarter() {
        return PostStarter.value(this.getClass(), LifecycleLevel.SYSTEM_LEVEL_1);
    }

    @Override
    public void postStart() throws Exception {
        this.monitor();
    }


}
