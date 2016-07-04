package com.tny.game.suite.cluster;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ThreadLocalRandom;

public class WebServiceNodeHolder {

    private ConcurrentNavigableMap<Integer, WebServiceNode> nodeMap = new ConcurrentSkipListMap<>();

    private volatile Integer lastKey = null;

    private String serverType;

    public WebServiceNodeHolder(String serverType) {
        super();
        this.serverType = serverType;
    }

    public String getServerType() {
        return serverType;
    }

    public synchronized void addNode(WebServiceNode node) {
        this.nodeMap.put(node.getServerID(), node);
        this.lastKey = this.nodeMap.lastKey();
    }

    public synchronized void removeNode(int serviceID) {
        this.nodeMap.remove(serviceID);
        if (!this.nodeMap.isEmpty())
            this.lastKey = this.nodeMap.lastKey();
        else
            this.lastKey = null;
    }

    public WebServiceNode randNode() {
        if (this.lastKey == null)
            return null;
        while (!this.nodeMap.isEmpty()) {
            int randValue = ThreadLocalRandom.current().nextInt(lastKey) + 1;
            Entry<Integer, WebServiceNode> entry = this.nodeMap.floorEntry(randValue);
            if (entry != null)
                return entry.getValue();
        }
        return null;
    }

    public Collection<WebServiceNode> getAllNode() {
        return this.nodeMap.values();
    }

    public WebServiceNode getNode(int serviceID) {
        return this.nodeMap.get(serviceID);
    }

    public String selectUrl() {
        WebServiceNode node = randNode();
        if (node != null)
            return node.getUrl();
        return null;
    }

    public String selectUrl(int id) {
        if (this.nodeMap.isEmpty())
            return null;
        Entry<Integer, WebServiceNode> entry = this.nodeMap.floorEntry(id);
        if (entry != null)
            return entry.getValue().getUrl();
        entry = this.nodeMap.firstEntry();
        if (entry != null)
            return entry.getValue().getUrl();
        return null;
    }

}
