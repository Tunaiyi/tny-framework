package com.tny.game.net.netty4.cloud.nacos;

import com.alibaba.nacos.api.naming.listener.*;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.utils.NamingUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.*;
import com.tny.game.common.concurrent.*;
import com.tny.game.common.lifecycle.*;
import com.tny.game.net.relay.cluster.*;
import com.tny.game.net.relay.cluster.watch.*;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/13 11:45 上午
 */
public abstract class BaseServeNodeClient implements ServeNodeClient, AppClosed {

    public static final Logger LOGGER = LoggerFactory.getLogger(NacosServeNodeClient.class);

    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1, new CoreThreadFactory("ServeNodeClient"));

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final Map<String, ServeNodeHolder> serveNodeHolderMap = new ConcurrentHashMap<>();

    @Override
    public List<ServeNode> getAllServeNodes(String serveName) {
        ServeNodeHolder holder = serveNodeHolderMap.get(serveName);
        if (holder == null) {
            return ImmutableList.of();
        }
        return as(holder.getAllNodes());
    }

    @Override
    public List<ServeNode> getHealthyServeNodes(String serveName) {
        ServeNodeHolder holder = serveNodeHolderMap.get(serveName);
        if (holder == null) {
            return ImmutableList.of();
        }
        return as(holder.getHealthyNodes());
    }

    @Override
    public ServeNode getServeNode(String serveName, long id) {
        ServeNodeHolder holder = serveNodeHolderMap.get(serveName);
        if (holder == null) {
            return null;
        }
        return holder.getNode(id);
    }

    @Override
    public ServeNode getHealthyServeNode(String serveName, long id) {
        ServeNodeHolder holder = serveNodeHolderMap.get(serveName);
        if (holder == null) {
            return null;
        }
        ServeNode node = holder.getNode(id);
        if (node != null && node.isHealthy()) {
            return node;
        }
        return null;
    }

    @Override
    public void subscribe(String serveName, ServeNodeListener listener) {
        ServeNodeHolder holder = serveNodeHolderMap.get(serveName);
        if (holder != null) {
            holder.addListener(listener);
        } else {
            ServeNodeHolder newHolder = new ServeNodeHolder(serveName);
            ServeNodeHolder oldOne = serveNodeHolderMap.putIfAbsent(serveName, newHolder);
            if (oldOne != null) {
                oldOne.addListener(listener);
            } else {
                newHolder.addListener(listener);
                newHolder.start();
            }
        }

    }

    @Override
    public void onClosed() {
        serveNodeHolderMap.forEach((k, holder) -> {
            holder.stop();
        });
    }

    @Override
    public void unsubscribe(String serveName, ServeNodeListener listener) {
        ServeNodeHolder holder = serveNodeHolderMap.get(serveName);
        if (holder != null) {
            holder.removeListener(listener);
        }
    }

    protected abstract void doSubscribe(String serveName);

    protected abstract void doUnsubscribe(String serveName);

    protected void handleEvent(Event event) {
        if (event instanceof NamingEvent) {
            this.handleChange((NamingEvent)event);
        }
    }

    private void handleChange(NamingEvent event) {
        String serviceName = NamingUtils.getServiceName(event.getServiceName());
        ServeNodeHolder holder = serveNodeHolderMap.get(serviceName);
        if (holder == null) {
            LOGGER.warn("serve {} is not exist", serviceName);
            return;
        }
        List<NacosRemoteServeNode> nodes = new ArrayList<>();
        for (Instance instance : event.getInstances()) {
            try {
                NacosRemoteServeNode node = new NacosRemoteServeNode(instance, OBJECT_MAPPER);
                nodes.add(node);
            } catch (Throwable e) {
                LOGGER.error("", e);
            }
        }
        holder.onChange(nodes);
    }

    private class ServeNodeHolder {

        private final String serveName;

        public final AtomicBoolean start = new AtomicBoolean(false);

        private volatile Map<Long, NacosRemoteServeNode> allNodeMap = ImmutableMap.of();

        private volatile List<NacosRemoteServeNode> allNodes = ImmutableList.of();

        private volatile List<NacosRemoteServeNode> healthyNodes = ImmutableList.of();

        private final List<ServeNodeListener> listeners = new CopyOnWriteArrayList<>();

        private ServeNodeHolder(String serveName) {
            this.serveName = serveName;
        }

        private String getServeName() {
            return serveName;
        }

        public ServeNode getNode(long id) {
            return allNodeMap.get(id);
        }

        private Map<Long, NacosRemoteServeNode> getAllNodeMap() {
            return allNodeMap;
        }

        private List<NacosRemoteServeNode> getAllNodes() {
            return allNodes;
        }

        private List<NacosRemoteServeNode> getHealthyNodes() {
            return healthyNodes;
        }

        private void start() {
            if (start.compareAndSet(false, true)) {
                try {
                    doSubscribe(this.serveName);
                } catch (Throwable e) {
                    LOGGER.error("Subscribe {} serve exception", this.serveName, e);
                    start.set(false);
                    executorService.schedule(this::start, 3000, TimeUnit.MILLISECONDS);
                }
            }
        }

        private void stop() {
            if (start.compareAndSet(true, false)) {
                try {
                    doUnsubscribe(this.serveName);
                } catch (Throwable e) {
                    LOGGER.error("Subscribe {} serve exception", this.serveName, e);
                    start.set(false);
                    executorService.schedule(this::start, 3000, TimeUnit.MILLISECONDS);
                }
            }
        }

        private void onChange(List<NacosRemoteServeNode> nodes) {
            Map<NacosRemoteServeNode, List<ServeNodeChangeStatus>> changes = new LinkedHashMap<>();
            List<NacosRemoteServeNode> creates = new ArrayList<>();
            List<NacosRemoteServeNode> healthyNodes = new ArrayList<>();
            Map<Long, NacosRemoteServeNode> newNodeMap = new HashMap<>();
            Map<Long, NacosRemoteServeNode> oldNodeMap = new HashMap<>(allNodeMap);
            for (NacosRemoteServeNode node : nodes) {
                NacosRemoteServeNode oldNode = oldNodeMap.remove(node.getId());
                if (oldNode == null) {
                    creates.add(node);
                } else {
                    List<ServeNodeChangeStatus> changeStatuses = ServeNodeChangeStatus.checkChange(oldNode, node);
                    if (!changeStatuses.isEmpty()) {
                        changes.put(node, changeStatuses);
                    }
                }
                newNodeMap.put(node.getId(), node);
                if (node.isHealthy()) {
                    healthyNodes.add(node);
                }
            }
            List<NacosRemoteServeNode> removes = new ArrayList<>(oldNodeMap.values());
            this.allNodeMap = ImmutableMap.copyOf(newNodeMap);
            this.allNodes = ImmutableList.copyOf(nodes);
            this.healthyNodes = ImmutableList.copyOf(healthyNodes);
            removes.forEach(node -> fire(ServeNodeListener::onRemove, node));
            creates.forEach(node -> fire(ServeNodeListener::onCreate, node));
            changes.forEach((node, statuses) -> fire((l, n) -> l.onChange(n, statuses), node));
        }

        private void addListener(ServeNodeListener listener) {
            this.listeners.add(listener);
        }

        private void removeListener(ServeNodeListener listener) {
            this.listeners.remove(listener);
        }

        private void fire(BiConsumer<ServeNodeListener, ServeNode> invoker, ServeNode node) {
            for (ServeNodeListener listener : listeners) {
                try {
                    invoker.accept(listener, node);
                } catch (Throwable e) {
                    LOGGER.error("", e);
                }
            }
        }

    }

}
