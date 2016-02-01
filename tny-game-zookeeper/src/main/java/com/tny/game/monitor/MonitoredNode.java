package com.tny.game.monitor;

import com.tny.game.zookeeper.MonitorTask;

public class MonitoredNode<D> {

    private D data;

    private MonitorTask monitorTask;

    private boolean monitored = false;

    private NodeDataFormatter formatter;

    public MonitoredNode(NodeDataFormatter formatter) {
        super();
        this.data = null;
        this.monitorTask = null;
        this.formatter = formatter;
    }

    public MonitorTask getMonitorTask() {
        return this.monitorTask;
    }

    public D getData() {
        return this.data;
    }

    protected void monitored() {
        if (!this.monitored) {
            this.monitored = true;
        }
    }

    protected boolean isMonitor() {
        return this.monitored;
    }

    protected void setMonitorTask(MonitorTask monitorTask) {
        this.monitorTask = monitorTask;
    }

    protected void change(byte[] data) {
        if (data.length == 0) {
            this.data = null;
        } else {
            this.data = this.formatter.bytes2Data(data);
        }
    }

}
