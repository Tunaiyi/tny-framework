package com.tny.game.suite.cluster;

import com.tny.game.common.event.bus.*;
import com.tny.game.suite.cluster.event.*;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.concurrent.*;

public class ServiceNodeHolder {

    private ConcurrentNavigableMap<Integer, ServiceNode> nodeMap = new ConcurrentSkipListMap<>();

    private static final BindP1EventBus<ServiceNodeHolderListener, ServiceNodeHolder, ServiceNode> ON_ADD = EventBuses.of(
            ServiceNodeHolderListener.class, ServiceNodeHolderListener::onAdd);

    private static final BindP1EventBus<ServiceNodeHolderListener, ServiceNodeHolder, ServiceNode> ON_REMOVE = EventBuses.of(
            ServiceNodeHolderListener.class, ServiceNodeHolderListener::onRemove);

    private static final BindP2EventBus<ServiceNodeHolderListener, ServiceNodeHolder, ServiceNode, ServiceNode> ON_CHANGE = EventBuses.of(
            ServiceNodeHolderListener.class, ServiceNodeHolderListener::onChange);

    private volatile Integer lastKey = null;

    private String appType;

    public ServiceNodeHolder(String appType) {
        super();
        this.appType = appType;
    }

    public String getAppType() {
        return appType;
    }

    public synchronized void addNode(ServiceNode node) {
        ServiceNode oldNode = this.nodeMap.put(node.getServerId(), node);
        if (oldNode == null)
            ON_ADD.notify(this, node);
        else
            ON_CHANGE.notify(this, node, oldNode);
        this.lastKey = this.nodeMap.lastKey();
    }

    public synchronized void removeNode(int serviceID) {
        ServiceNode node = this.nodeMap.remove(serviceID);
        if (node != null)
            ON_REMOVE.notify(this, node);
        if (!this.nodeMap.isEmpty())
            this.lastKey = this.nodeMap.lastKey();
        else
            this.lastKey = null;
    }

    public ServiceNode randNode() {
        if (this.lastKey == null)
            return null;
        while (!this.nodeMap.isEmpty()) {
            int randValue = ThreadLocalRandom.current().nextInt(lastKey) + 1;
            Entry<Integer, ServiceNode> entry = this.nodeMap.floorEntry(randValue);
            if (entry != null)
                return entry.getValue();
        }
        return null;
    }

    public ServiceNode getByHash(int hashKey) {
        return null;
    }

    public ServiceNode getByRoll(int hashKey) {
        return null;
    }

    public Collection<ServiceNode> getAllNode() {
        return this.nodeMap.values();
    }

    public ServiceNode getNode(int serviceID) {
        return this.nodeMap.get(serviceID);
    }


    // public URL randURL(String urlProtocol) {
    //     ServiceNode node = randNode();
    //     if (node != null)
    //         return node.getURL(urlProtocol);
    //     return null;
    // }
    //
    // public URL selectURL(String urlProtocol, int id) {
    //     if (this.nodeMap.isEmpty())
    //         return null;
    //     Entry<Integer, ServiceNode> entry = this.nodeMap.floorEntry(id);
    //     if (entry != null)
    //         return entry.getValue().getURL(urlProtocol);
    //     entry = this.nodeMap.firstEntry();
    //     if (entry != null)
    //         return entry.getValue().getURL(urlProtocol);
    //     return null;
    // }

}
