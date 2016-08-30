package com.tny.game.suite.cluster.game;


import com.tny.game.common.config.Config;
import com.tny.game.event.BindP1EventBus;
import com.tny.game.event.EventBuses;
import com.tny.game.net.config.BindIp;
import com.tny.game.net.config.ServerConfig;
import com.tny.game.net.config.ServerConfigFactory;
import com.tny.game.net.initer.InitLevel;
import com.tny.game.net.initer.PostIniter;
import com.tny.game.net.initer.ServerPostStart;
import com.tny.game.number.NumberUtils;
import com.tny.game.suite.cluster.BaseCluster;
import com.tny.game.suite.cluster.ClusterUtils;
import com.tny.game.suite.cluster.event.GameServerClusterListener;
import com.tny.game.suite.core.GameInfo;
import com.tny.game.suite.initer.ProtoExSchemaIniter;
import com.tny.game.suite.utils.Configs;
import com.tny.game.zookeeper.NodeWatcher;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    private ServerConfigFactory factory;

    @Autowired
    private ProtoExSchemaIniter initer;

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
        super();
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
        ServerConfig context = this.factory.getServerContext();
        List<BindIp> serverIPList = context.getBindIp(null);
        BindIp ip = serverIPList.get(0);
        List<ServerOutline> outlines = new ArrayList<>();
        GameInfo main = GameInfo.getMainInfo();
        for (GameInfo info : GameInfo.getAllGamesInfo()) {
            if (!info.isRegister())
                continue;
            Config config = context.getConfig();
            ServerOutline outline = new ServerOutline()
                    .setServerID(info.getServerID())
                    .setServerScope(info.getScopeType().getName())
                    .setServerType(info.getScopeType().getServerType().getName())
                    .setMain(info.isMainServer())
                    .setOpenDate(info.getOpenDate())
                    .setServerPort(ip.getPorts().get(0))
                    .setPublicIP(config.getStr(Configs.PUBLIC_HOST))
                    .setPrivateIP(config.getStr(Configs.PRIVATE_HOST))
                    .setRmiPort(config.getInt(Configs.RMI_PORT))
                    .setDbHost(System.getProperty(DB_HOST))
                    .setDbPort(NumberUtils.toInt(System.getProperty(DB_PORT, "0")))
                    .setDb(System.getProperty(DB_NAME))
                    .setFollowServer(main.getServerID());
            outlines.add(outline);
        }
        return outlines;
    }

    @Override
    public PostIniter getIniter() {
        return PostIniter.initer(this.getClass(), InitLevel.LEVEL_1);
    }

    @Override
    public void initialize() throws Exception {
        if (this.initer.waitInitialized())
            this.monitor();
    }

    @Override
    public boolean waitInitialized() {
        return true;
    }

}