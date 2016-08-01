package com.tny.game.suite.cluster;


import com.tny.game.suite.cluster.game.ServerLaunch;
import com.tny.game.suite.cluster.game.ServerOutline;
import com.tny.game.suite.cluster.game.ServerSetting;
import com.tny.game.suite.initer.ProtoExSchemaIniter;
import com.tny.game.suite.utils.Configs;
import com.tny.game.zookeeper.NodeWatcher;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class WebServerCluster extends BaseCluster {

    @Autowired
    private ProtoExSchemaIniter protoExSchemaIniter;

    protected int webServerID;

    protected String serverType;

    protected boolean watchSetting;

    protected ConcurrentMap<Integer, ServerNode> nodeMap = new ConcurrentHashMap<>();

    protected NodeWatcher<ServerLaunch> launchHandler = (path, state, old, data) -> {
        ServerNode node = null;
        switch (state) {
            case CREATE:
                if (data == null)
                    return;
                node = setLaunch(data.getServerID(), data, true);
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("path : {} | 服务器 {} 上线! {} ", path, node.getServerID(), node);
                break;
            case CHANGE:
                if (data == null)
                    return;
                node = this.setLaunch(data.getServerID(), data, false);
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("path : {} | 服务器 {} 上线! {} ", path, data.getServerID(), node);
                break;
            case DELETE:
                if (old == null)
                    return;
                node = this.removeLaunch(old.getServerID());
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("path : {} | 服务器 {} 下线! {}", path, node.getServerID());
                break;
        }
    };

    protected NodeWatcher<ServerOutline> outlineHandler = (path, state, old, data) -> {
        ServerNode node = null;
        switch (state) {
            case CREATE:
                if (data == null)
                    return;
                node = this.setOutline(data);
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("path : {} | 服务器 {} Outline 创建! {} ", path, data.getServerID(), node);
                break;
            case CHANGE:
                if (data == null)
                    return;
                node = this.setOutline(data);
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("path : {} | 服务器 {} Outline 改变! {} ", path, data.getServerID(), node);
                break;
            case DELETE:
                if (old == null)
                    return;
                node = removeOutline(old.getServerID());
                if (node != null && LOGGER.isDebugEnabled())
                    LOGGER.debug("path : {} | 服务器 {} Outline 删除!", path, node.getServerID());
                break;
        }
    };

    protected NodeWatcher<ServerSetting> settingHandler = (path, state, old, data) -> {
        if (data == null)
            return;
        ServerNode node = null;
        switch (state) {
            case CREATE:
                if (data == null)
                    return;
                node = this.setSetting(data.getServerID(), data, true);
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("path : {} | 服务器 {} Setting 创建! {} ", path, data.getServerID(), node);
                break;
            case CHANGE:
                if (data == null)
                    return;
                node = this.setSetting(data.getServerID(), data, false);
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("path : {} | 服务器 {} Setting 改变! {} ", path, data.getServerID(), node);
                break;
            case DELETE:
                if (old == null)
                    return;
                node = removeSetting(old.getServerID());
                if (node != null && LOGGER.isDebugEnabled())
                    LOGGER.debug("path : {} | 服务器 {} Setting 删除!", path, node.getServerID());
                break;
        }
    };

    @Override
    @PostConstruct
    protected void init() throws IOException, KeeperException, InterruptedException {
        if (this.protoExSchemaIniter.waitInitialized())
            super.monitor();
    }

    public WebServerCluster(String serverType, int webServerID, boolean watchSetting, String... monitorWebTypes) {
        this(serverType, webServerID, watchSetting, Arrays.asList(monitorWebTypes));
    }

    public WebServerCluster(String serverType, int webServerID, boolean watchSetting, Collection<String> monitorWebTypes) {
        super(monitorWebTypes);
        this.serverType = serverType;
        this.watchSetting = watchSetting;
        this.webServerID = webServerID;
    }

    public List<ServerNode> getAllServerNodes() {
        return new ArrayList<>(this.nodeMap.values());
    }

    public ServerNode getServerNode(int serverID) {
        return this.nodeMap.get(serverID);
    }

    protected void updateSetting(ServerNode node, ServerSetting setting) {
        node.setSetting(setting);
    }

    @Override
    public void doMonitor() {
        this.remoteMonitor.createFullNode(ClusterUtils.OUTLINE_LIST_PATH, this.NOTHING, CreateMode.PERSISTENT, false);
        this.remoteMonitor.createFullNode(ClusterUtils.SETTING_LIST_PATH, this.NOTHING, CreateMode.PERSISTENT, false);
        this.remoteMonitor.createFullNode(ClusterUtils.GAMES_LAUNCH_PATH, this.NOTHING, CreateMode.PERSISTENT, false);
        this.remoteMonitor.monitorChildren(ClusterUtils.OUTLINE_LIST_PATH, this.outlineHandler);
        this.remoteMonitor.monitorChildren(ClusterUtils.GAMES_LAUNCH_PATH, this.launchHandler);
        if (this.watchSetting)
            this.remoteMonitor.monitorChildren(ClusterUtils.SETTING_LIST_PATH, this.settingHandler);

        String host = Configs.SERVICE_CONFIG.getStr(Configs.SERVICE_CONFIG_WEB_SERVICE_HOST);
        String port = Configs.SERVICE_CONFIG.getStr(Configs.SERVICE_CONFIG_WEB_SERVICE_PORT);
        String part = this.getWebServiceNodePath();
        String url = "http://" + host + ":" + port + part;
        WebServiceNode node = new WebServiceNode(this.webServerID, url);
        String nodePath = ClusterUtils.getWebNodePath(this.serverType, this.webServerID);
        this.remoteMonitor.putNodeData(CreateMode.EPHEMERAL, node, nodePath);
        LOGGER.info("注册 {} web service url [ {} ] 到 {}", this.serverType, url, nodePath);
    }

    public int getWebServerID() {
        return this.webServerID;
    }

    public Integer getMainServerID(Integer serverID) {
        ServerNode node = this.getServerNode(serverID);
        if (node != null) {
            ServerOutline outline = node.getOutline();
            if (outline == null)
                return null;
            return outline.getFollowServer();
        }
        return null;
    }

    public Set<Integer> getMainServerIDs(Collection<Integer> serverIDs) {
        Set<Integer> mains = new HashSet<>();
        for (Integer serverID : serverIDs) {
            ServerNode node = this.getServerNode(serverID);
            if (node != null && node.isHasOutline()) {
                ServerOutline outline = node.getOutline();
                mains.add(outline.getFollowServer());
            }
        }
        return mains;
    }

    public abstract String getWebServiceNodePath();

    private ServerNode setOutline(ServerOutline outline) {
        ServerNode node = this.nodeMap().get(outline.getServerID());
        if (node != null) {
            node.setOutline(outline);
            this.postUpdateOutline(node, outline, false);
        } else {
            node = new ServerNode();
            node.setOutline(outline);
            this.postUpdateOutline(node, outline, true);
            this.nodeMap().put(node.getServerID(), node);
        }
        return node;
    }

    private ServerNode removeOutline(int serverID) {
        ServerNode node = this.nodeMap().remove(serverID);
        if (node != null)
            postRemoveOutline(node);
        return node;
    }

    private ServerNode setLaunch(int serverID, ServerLaunch launch, boolean create) {
        ServerNode node = this.nodeMap().get(serverID);
        if (node != null) {
            node.setLaunch(launch);
            if (launch != null)
                this.postUpdateLaunch(node, launch, create);
        }
        return node;
    }

    private ServerNode removeLaunch(int serverID) {
        ServerNode node = this.nodeMap().remove(serverID);
        if (node != null) {
            node.setLaunch(null);
            this.postRemoveLaunch(node);
        }
        return node;
    }


    private ServerNode setSetting(int serverID, ServerSetting setting, boolean create) {
        ServerNode node = this.nodeMap().get(serverID);
        if (node != null) {
            node.setSetting(setting);
            if (setting != null)
                this.postUpdateSetting(node, setting, create);
        }
        return node;
    }

    private ServerNode removeSetting(int serverID) {
        ServerNode node = this.nodeMap().get(serverID);
        if (node != null) {
            node.setSetting(null);
            this.postRemoveSetting(node);
        }
        return node;
    }

    private ConcurrentMap<Integer, ServerNode> nodeMap() {
        return this.nodeMap;
    }


    protected void postRemoveOutline(ServerNode node) {
    }

    protected void postUpdateOutline(ServerNode node, ServerOutline outline, boolean create) {

    }

    protected void postRemoveSetting(ServerNode node) {
    }

    protected void postUpdateSetting(ServerNode node, ServerSetting serverSetting, boolean create) {

    }

    protected void postRemoveLaunch(ServerNode node) {
    }

    protected void postUpdateLaunch(ServerNode node, ServerLaunch launch, boolean create) {

    }

}
