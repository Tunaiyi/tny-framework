package com.tny.game.suite.cluster;

import com.tny.game.zookeeper.NodeWatcher;
import com.tny.game.zookeeper.ZKMonitor;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class BaseCluster {

    protected final Logger LOGGER;

    protected ZKMonitor remoteMonitor;

    protected final byte[] NOTHING = new byte[0];

    protected Set<String> monitorServerTypes = new HashSet<>();

    protected ConcurrentMap<String, WebServiceNodeHolder> webHolderMap = new ConcurrentHashMap<>();

    public BaseCluster(String... monitorServerTypes) {
        this(Arrays.asList(monitorServerTypes));
    }

    public BaseCluster(Collection<String> monitorServerTypes) {
        this.LOGGER = LoggerFactory.getLogger(this.getClass());
        this.monitorServerTypes.addAll(monitorServerTypes);
    }

    public Collection<WebServiceNodeHolder> getAllWebHolders() {
        return this.webHolderMap.values();
    }

    protected void init() throws Exception {
        this.monitor();
    }

    protected void doMonitor() {
    }

    protected void monitor() throws IOException, KeeperException, InterruptedException {
        if (this.remoteMonitor == null) {
            String ips = System.getProperty(ClusterUtils.IP_LIST);
            if (ips == null)
                throw new NullPointerException("RemoteMonitor IP is null");
            this.remoteMonitor = new ZKMonitor(ips, ClusterUtils.PROTO_FORMATTER);
            for (String serverType : this.monitorServerTypes) {
                WebServiceNodeHolder holder = new WebServiceNodeHolder(serverType);
                String path = ClusterUtils.getWebNodesPath(serverType);
                this.webHolderMap.put(serverType, holder);
                this.remoteMonitor.monitorChildren(path, createWebServerWatcher(serverType.toString(), holder));
            }
            this.doMonitor();
        }

    }

    private NodeWatcher<WebServiceNode> createWebServerWatcher(String name, WebServiceNodeHolder holder) {
        return (path, state, old, data) -> {
            switch (state) {
                case CREATE:
                    if (data == null)
                        return;
                    holder.addNode(data);
                    if (LOGGER.isDebugEnabled())
                        LOGGER.debug("path : {} | {} 服务器 {} 上线! {} ", path, name, data.getServerID(), data.getUrl());
                    break;
                case CHANGE:
                    if (data == null)
                        return;
                    holder.addNode(data);
                    if (LOGGER.isDebugEnabled())
                        LOGGER.debug("path : {} | {} 服务器 {} 更新! {} ", path, name, data.getServerID(), data.getUrl());
                    break;
                case DELETE:
                    if (old == null)
                        return;
                    holder.removeNode(old.getServerID());
                    if (LOGGER.isDebugEnabled())
                        LOGGER.debug("path : {} | {} 服务器下线!", path, name, old.getServerID(), old.getUrl());
                    break;
            }
        };
    }

    public Optional<String> webUrl(String type, String path) {
        WebServiceNodeHolder holder = this.webHolderMap.get(type);
        if (holder == null)
            return Optional.empty();
        return Optional.of(holder.selectUrl() + path);
    }

    public Optional<String> webUrl(String type, int id, String path) {
        WebServiceNodeHolder holder = this.webHolderMap.get(type);
        if (holder == null)
            return Optional.empty();
        WebServiceNode node = holder.getNode(id);
        if (node == null)
            return Optional.empty();
        return Optional.of(node.getUrl() + path);
    }

    public Optional<String> webUrl(WebPath path) {
        return webUrl(path.getServerType(), path.getPath());
    }

}
