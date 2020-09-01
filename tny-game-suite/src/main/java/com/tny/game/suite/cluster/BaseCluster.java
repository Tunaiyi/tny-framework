package com.tny.game.suite.cluster;

import com.google.common.collect.ImmutableList;
import com.tny.game.common.url.*;
import com.tny.game.common.utils.*;
import com.tny.game.zookeeper.*;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;

public abstract class BaseCluster {

    protected final Logger LOGGER;

    protected ZKMonitor remoteMonitor;

    protected final byte[] NOTHING = new byte[0];

    protected boolean monitorAllServices;

    protected Set<String> monitorServerTypes = new HashSet<>();

    protected ConcurrentMap<String, ServiceNodeHolder> webHolderMap = new ConcurrentHashMap<>();

    public BaseCluster(String... monitorAppTypes) {
        this(false, Arrays.asList(monitorAppTypes));
    }

    public BaseCluster(boolean monitorAllServices) {
        this(monitorAllServices, ImmutableList.of());
    }

    public BaseCluster(Collection<String> monitorAppTypes) {
        this(false, monitorAppTypes);
    }

    protected BaseCluster(boolean monitorAllServices, Collection<String> monitorAppTypes) {
        this.LOGGER = LoggerFactory.getLogger(this.getClass());
        this.monitorAllServices = monitorAllServices;
        this.monitorServerTypes.addAll(monitorAppTypes);
    }

    public Collection<ServiceNodeHolder> getAllNodeHolders() {
        return this.webHolderMap.values();
    }

    public Optional<ServiceNodeHolder> getNodeHolder(String appType) {
        return Optional.of(this.webHolderMap.get(appType));
    }

    protected void doMonitor() {
    }

    protected abstract List<ZKMonitorInitHandler> initHandlers();

    protected void monitor() throws Exception {
        if (this.remoteMonitor == null) {
            String ips = System.getProperty(ClusterUtils.IP_LIST);
            if (ips == null)
                throw new NullPointerException("RemoteMonitor IP is null");
            this.remoteMonitor = new ZKMonitor(ips, ClusterUtils.PROTO_FORMATTER);
            if (this.monitorAllServices) {
                ClusterUtils.getAllWebNodesPaths().forEach((k, v) -> {
                    Matcher matcher = ClusterUtils.WS_NOTES_PATH_REGEX.matcher(k);
                    if (matcher.find()) {
                        String serverType = matcher.group(1);
                        ServiceNodeHolder holder = new ServiceNodeHolder(serverType);
                        this.webHolderMap.put(serverType, holder);
                        this.remoteMonitor.monitorChildren(v, createWebServerWatcher(serverType, holder));
                    }
                });
            } else {
                for (String serverType : this.monitorServerTypes) {
                    ServiceNodeHolder holder = new ServiceNodeHolder(serverType);
                    String path = ClusterUtils.getWebNodesPath(serverType);
                    this.webHolderMap.put(serverType, holder);
                    this.remoteMonitor.monitorChildren(path, createWebServerWatcher(serverType, holder));
                }
            }
            this.doMonitor();
            this.initHandlers().forEach(h -> h.onInit(this.remoteMonitor));
        }

    }

    protected void register() {
    }

    private NodeWatcher<ServiceNode> createWebServerWatcher(String name, ServiceNodeHolder holder) {
        return (path, state, old, data) -> {
            switch (state) {
                case CREATE:
                    if (data == null)
                        return;
                    holder.addNode(data);
                    if (this.LOGGER.isDebugEnabled())
                        this.LOGGER.debug("path : {} | {} 服务器 {} 上线! {} ", path, name, data.getServerId(), data.getUrlMap());
                    break;
                case CHANGE:
                    if (data == null)
                        return;
                    holder.addNode(data);
                    if (this.LOGGER.isDebugEnabled())
                        this.LOGGER.debug("path : {} | {} 服务器 {} 更新! {} ", path, name, data.getServerId(), data.getUrlMap());
                    break;
                case DELETE:
                    if (old == null)
                        return;
                    holder.removeNode(old.getServerId());
                    if (this.LOGGER.isDebugEnabled())
                        this.LOGGER.debug("path : {} | {} 服务器下线!", path, name, old.getServerId(), old.getUrlMap());
                    break;
            }
        };
    }

    public String urlByRand(String urlProtocol, String type, String path) {
        ServiceNodeHolder holder = this.webHolderMap.get(type);
        ThrowAide.checkArgument(holder != null, "获取 Protocol : {} | Path : [{}] 时, 没有找到与 {} 对应的ServiceNodeHolder", urlProtocol, path, type);
        ServiceNode node = holder.randNode();
        ThrowAide.checkArgument(node != null, "获取 Protocol : {} | Path : [{}] 时, 没有找到符合的服务器节点", urlProtocol, path);
        URL url = node.getURL(urlProtocol);
        ThrowAide.checkArgument(url != null, "获取 Protocol : {} | Path : [{}] 时, {} 服务器节点 {} 没有对应的URL", urlProtocol, path, node.getAppType(),
                node.getServerId());
        return url.toString() + path;
    }

    public String url(String urlProtocol, String type, int id, String path) {
        ServiceNodeHolder holder = this.webHolderMap.get(type);
        ThrowAide.checkArgument(holder != null, "获取 Protocol : {} | Path : [{}] 时, 没有找到与 {} 对应的ServiceNodeHolder", urlProtocol, path, type);
        ServiceNode node = holder.getNode(id);
        ThrowAide.checkArgument(node != null, "获取 Protocol : {} | Path : [{}] 时, 没有找到 {} 服务器节点 {}", urlProtocol, path, type, id);
        URL url = node.getURL(urlProtocol);
        ThrowAide.checkArgument(url != null, "获取 Protocol : {} | Path : [{}] 时, {} 服务器节点 {} 没有对应的URL", urlProtocol, path, node.getAppType(),
                node.getServerId());
        return url.toString() + path;
    }


    public String urlByRand(AppURLPath path) {
        return urlByRand(path.getProtocol(), path.getAppType(), path.getPath());
    }

}
