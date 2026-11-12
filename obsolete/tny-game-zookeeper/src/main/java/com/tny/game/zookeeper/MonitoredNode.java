/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
