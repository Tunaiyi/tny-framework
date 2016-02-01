package com.tny.game.monitor;

public interface NodeHandler<T> {

    public boolean isCanHandle(String path);

    public void notifyNodeCreaate(NodeEvent<T> event);

    public void notifyNodeChange(NodeEvent<T> event);

    public void notifyNodeDelected(NodeEvent<T> event);

}
