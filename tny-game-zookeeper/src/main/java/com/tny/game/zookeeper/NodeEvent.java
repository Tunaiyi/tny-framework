package com.tny.game.zookeeper;

public class NodeEvent<T> {

    private String path;

    private MonitoredNode<T> node;

    public NodeEvent(String path, MonitoredNode<T> node) {
        super();
        this.path = path;
        this.node = node;
    }

    public String getPath() {
        return this.path;
    }

    public MonitoredNode<T> getNode() {
        return this.node;
    }
}
