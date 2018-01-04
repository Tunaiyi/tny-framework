package com.tny.game.suite.cluster;


import com.google.common.collect.*;
import com.tny.game.common.lifecycle.*;
import com.tny.game.suite.cluster.game.*;
import com.tny.game.suite.initer.*;
import com.tny.game.suite.utils.*;
import com.tny.game.zookeeper.*;
import org.apache.commons.lang3.*;
import org.apache.zookeeper.*;

import javax.annotation.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

import static com.tny.game.suite.utils.Configs.*;

public abstract class ServiceCluster extends SpringBaseCluster implements ServerPostStart {

    @Resource
    private ProtoExSchemaIniter protoExSchemaIniter;

    protected int webServerID;

    protected String serverType;

    protected boolean watchSetting;

    protected ConcurrentMap<Integer, ServerNode> nodeMap = new ConcurrentHashMap<>();

    protected NodeWatcher<ServerLaunch> launchHandler = (path, state, old, data) -> {
        ServerNode node;
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
                    LOGGER.debug("path : {} | 服务器 {} 下线! {}", path, old.getServerID(), node);
                break;
        }
    };

    protected NodeWatcher<ServerOutline> outlineHandler = (path, state, old, data) -> {
        ServerNode node;
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
                    LOGGER.debug("path : {} | 服务器 {} Outline 删除!", path, old.getServerID());
                break;
        }
    };

    protected NodeWatcher<ServerSetting> settingHandler = (path, state, old, data) -> {
        ServerNode node;
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
                    LOGGER.debug("path : {} | 服务器 {} Setting 删除!", path, old.getServerID());
                break;
        }
    };

    @Override
    public void prepareStart() throws Throwable {
        super.prepareStart();
        this.protoExSchemaIniter.prepareStart();
        this.monitor();
    }


    public ServiceCluster(String serverType, boolean watchSetting, String... monitorWebTypes) {
        this(serverType, watchSetting, false, Arrays.asList(monitorWebTypes));
    }

    public ServiceCluster(String serverType, boolean watchSetting, Collection<String> monitorWebTypes) {
        this(serverType, watchSetting, false, monitorWebTypes);
    }

    public ServiceCluster(String serverType, boolean watchSetting, boolean monitorAllServices) {
        this(serverType, watchSetting, monitorAllServices, ImmutableList.of());
    }


    protected ServiceCluster(String serverType, boolean watchSetting, boolean monitorAllServices, Collection<String> monitorWebTypes) {
        super(monitorAllServices, monitorWebTypes);
        int serverID = Configs.SERVICE_CONFIG.getInt(Configs.SERVER_ID);
        this.serverType = serverType;
        this.watchSetting = watchSetting;
        this.webServerID = serverID;
    }

    public List<ServerNode> getAllServerNodes() {
        return new ArrayList<>(this.nodeMap.values());
    }

    public List<ServerNode> getValidServerNodes() {
        return this.nodeMap.values().stream()
                .filter(node -> node.isWork() && node.isInOpenDate() && node.getOutline() != null)
                .collect(Collectors.toList());
    }

    public ServerNode getServerNode(int serverID) {
        return this.nodeMap.get(serverID);
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

        this.postWebMonitor();
    }

    @Override
    protected void register() {
        String[] urls = clusterUrls();
        ServiceNode node = new ServiceNode(this.serverType, this.webServerID, urls);
        String nodePath = ClusterUtils.getWebNodePath(this.serverType, this.webServerID);
        this.remoteMonitor.putNodeData(CreateMode.EPHEMERAL, node, nodePath);
        LOGGER.info("注册 {} web service urls [\n{}\n] 到 {}", this.serverType, StringUtils.join(urls, "\n"), nodePath);
    }

    protected String[] clusterUrls() {
        Map<String, String> urls = SERVICE_CONFIG.find(Configs.SERVER_URL + ".*");
        return urls.values().toArray(new String[0]);
    }

    protected void postWebMonitor() {
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

    private ServerNode setOutline(ServerOutline outline) {
        ServerNode node = this.getOrCreate(outline.getServerID());
        if (node != null) {
            node.setOutline(outline);
            this.postUpdateOutline(node, outline, false);
        } else {
            node = new ServerNode(outline.getServerID());
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
        ServerNode node = this.getOrCreate(serverID);
        if (node != null) {
            node.setLaunch(launch);
            if (launch != null)
                this.postUpdateLaunch(node, launch, create);
        }
        return node;
    }

    private ServerNode removeLaunch(int serverID) {
        ServerNode node = this.getOrCreate(serverID);
        if (node != null) {
            node.setLaunch(null);
            this.postRemoveLaunch(node);
        }
        return node;
    }


    protected void updateSetting(ServerNode node, ServerSetting setting) {
        node.setSetting(setting);
    }

    private ServerNode setSetting(int serverID, ServerSetting setting, boolean create) {
        ServerNode node = this.getOrCreate(serverID);
        if (node != null) {
            node.setSetting(setting);
            if (setting != null)
                this.postUpdateSetting(node, setting, create);
        }
        return node;
    }

    private ServerNode removeSetting(int serverID) {
        ServerNode node = this.getOrCreate(serverID);
        if (node != null) {
            node.setSetting(null);
            this.postRemoveSetting(node);
        }
        return node;
    }

    private ServerNode getOrCreate(int serverID) {
        ServerNode node = this.nodeMap.get(serverID);
        if (node == null) {
            node = new ServerNode(serverID);
            node = ObjectUtils.defaultIfNull(this.nodeMap.putIfAbsent(serverID, node), node);
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

    public Optional<String> gameUrl(int serverID, String... paths) {
        ServerNode node = getServerNode(serverID);
        if (node == null)
            return Optional.empty();
        return node.getPrivateConnector(AppURLPaths.HTTP_INSIDE)
                .map(c -> {
                    StringBuilder urlBuilder = new StringBuilder().append("http://").append(c.getHost()).append(":").append(c.getPort());
                    for (String path : paths)
                        urlBuilder.append(path.startsWith("/") ? "" : "/").append(path);
                    return urlBuilder.toString();
                });
    }

    public Optional<String> gameUrl(int serverID, AppURLPath path) {
        return gameUrl(serverID, path.getPath());
    }

}
