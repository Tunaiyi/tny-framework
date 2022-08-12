/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.zookeeper;

import org.slf4j.*;

public class MonitoredNode<D> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZKMonitor.class);

    private D data;

    private MonitorTask monitorTask;

    private boolean monitored = false;

    private final NodeDataFormatter formatter;

    private final NodeWatcher<D> watcher;

    public MonitoredNode(NodeDataFormatter formatter, NodeWatcher<D> watcher) {
        super();
        this.data = null;
        this.monitorTask = null;
        this.formatter = formatter;
        this.watcher = watcher;
    }

    public D getData() {
        return this.data;
    }

    protected void monitored() {
        if (!this.monitored) {
            this.monitored = true;
        }
    }

    public boolean isMonitor() {
        return this.monitored;
    }

    void setMonitorTask(MonitorTask monitorTask) {
        this.monitorTask = monitorTask;
    }

    private void notify(String path, WatchState state, D oldDate, D newDate) {
        if (watcher != null) {
            this.watcher.notify(path, state, oldDate, newDate);
        }
    }

    void change(byte[] data) {
        boolean monitored = this.monitored;
        if (!monitored) {
            this.monitored = true;
        }
        D old = this.data;
        if (data.length == 0) {
            this.data = null;
        } else {
            this.data = this.formatter.bytes2Data(data);
        }
        this.notify(monitorTask.getPath(), !monitored ? WatchState.CREATE : WatchState.CHANGE, old, this.data);
    }

    void remove() {
        this.monitorTask.cancel();
        D old = this.data;
        this.data = null;
        LOGGER.debug("移除监听服务器节点 {}", this.monitorTask.getPath());
        this.notify(monitorTask.getPath(), WatchState.DELETE, old, this.data);
    }

}
