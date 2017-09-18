package com.tny.game.suite.cluster;

import com.tny.game.common.utils.Throws;
import com.tny.game.common.utils.URL;
import com.tny.game.zookeeper.NodeWatcher;
import com.tny.game.zookeeper.ZKMonitor;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class BaseCluster {

    protected final Logger LOGGER;

    protected ZKMonitor remoteMonitor;

    protected final byte[] NOTHING = new byte[0];

    protected Set<String> monitorServerTypes = new HashSet<>();

    protected ConcurrentMap<String, ServiceNodeHolder> webHolderMap = new ConcurrentHashMap<>();

    public BaseCluster(String... monitorAppTypes) {
        this(Arrays.asList(monitorAppTypes));
    }

    public BaseCluster(Collection<String> monitorAppTypes) {
        this.LOGGER = LoggerFactory.getLogger(this.getClass());
        this.monitorServerTypes.addAll(monitorAppTypes);
    }

    public Collection<ServiceNodeHolder> getAllNodeHolders() {
        return this.webHolderMap.values();
    }

    public Optional<ServiceNodeHolder> getNodeHolder(String appType) {
        return Optional.of(this.webHolderMap.get(appType));
    }

    protected void init() throws Exception {
        this.monitor();
    }

    protected void doMonitor() {
    }

    protected abstract List<ZKMonitorInitHandler> initHandlers();

    protected void monitor() throws IOException, KeeperException, InterruptedException {
        if (this.remoteMonitor == null) {
            String ips = System.getProperty(ClusterUtils.IP_LIST);
            if (ips == null)
                throw new NullPointerException("RemoteMonitor IP is null");
            this.remoteMonitor = new ZKMonitor(ips, ClusterUtils.PROTO_FORMATTER);
            for (String serverType : this.monitorServerTypes) {
                ServiceNodeHolder holder = new ServiceNodeHolder(serverType);
                String path = ClusterUtils.getWebNodesPath(serverType);
                this.webHolderMap.put(serverType, holder);
                this.remoteMonitor.monitorChildren(path, createWebServerWatcher(serverType, holder));
            }
            this.doMonitor();
            this.initHandlers().forEach(h -> h.onInit(this.remoteMonitor));
        }

    }

    private NodeWatcher<ServiceNode> createWebServerWatcher(String name, ServiceNodeHolder holder) {
        return (path, state, old, data) -> {
            switch (state) {
                case CREATE:
                    if (data == null)
                        return;
                    holder.addNode(data);
                    if (LOGGER.isDebugEnabled())
                        LOGGER.debug("path : {} | {} 服务器 {} 上线! {} ", path, name, data.getServerID(), data.getUrlMap());
                    break;
                case CHANGE:
                    if (data == null)
                        return;
                    holder.addNode(data);
                    if (LOGGER.isDebugEnabled())
                        LOGGER.debug("path : {} | {} 服务器 {} 更新! {} ", path, name, data.getServerID(), data.getUrlMap());
                    break;
                case DELETE:
                    if (old == null)
                        return;
                    holder.removeNode(old.getServerID());
                    if (LOGGER.isDebugEnabled())
                        LOGGER.debug("path : {} | {} 服务器下线!", path, name, old.getServerID(), old.getUrlMap());
                    break;
            }
        };
    }

    public String urlByRand(String urlProtocol, String type, String path) {
        ServiceNodeHolder holder = this.webHolderMap.get(type);
        Throws.checkArgument(holder != null, "获取 Protocol : {} | Path : [{}] 时, 没有找到与 {} 对应的ServiceNodeHolder", urlProtocol, path, type);
        ServiceNode node = holder.randNode();
        Throws.checkArgument(node != null, "获取 Protocol : {} | Path : [{}] 时, 没有找到符合的服务器节点", urlProtocol, path);
        URL url = node.getURL(urlProtocol);
        Throws.checkArgument(url != null, "获取 Protocol : {} | Path : [{}] 时, {} 服务器节点 {} 没有对应的URL", urlProtocol, path, node.getAppType(), node.getServerID());
        return url.toString() + path;
    }

    public String url(String urlProtocol, String type, int id, String path) {
        ServiceNodeHolder holder = this.webHolderMap.get(type);
        Throws.checkArgument(holder != null, "获取 Protocol : {} | Path : [{}] 时, 没有找到与 {} 对应的ServiceNodeHolder", urlProtocol, path, type);
        ServiceNode node = holder.getNode(id);
        Throws.checkArgument(node != null, "获取 Protocol : {} | Path : [{}] 时, 没有找到 {} 服务器节点 {}", urlProtocol, path, type, id);
        URL url = node.getURL(urlProtocol);
        Throws.checkArgument(url != null, "获取 Protocol : {} | Path : [{}] 时, {} 服务器节点 {} 没有对应的URL", urlProtocol, path, node.getAppType(), node.getServerID());
        return url.toString() + path;
    }


    public String urlByRand(AppURLPath path) {
        return urlByRand(path.getProtocol(), path.getAppType(), path.getPath());
    }

}
