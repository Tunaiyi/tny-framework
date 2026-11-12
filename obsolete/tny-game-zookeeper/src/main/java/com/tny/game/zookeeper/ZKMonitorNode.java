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

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class ZKMonitorNode<D> {

    private static final byte[] EMPTY_BYTES = new byte[0];

    private D data = null;

    private Stat keeperStat = new Stat();

    private volatile SyncStat syncStat = SyncStat.NEW;

    private String path = "";

    private CreateMode createMode;

    private NodeDataFormatter formatter;

    @SuppressWarnings("rawtypes")
    private static final AtomicReferenceFieldUpdater<ZKMonitorNode, SyncStat> stateUpdater = AtomicReferenceFieldUpdater
            .newUpdater(ZKMonitorNode.class, SyncStat.class, "syncStat");

    public ZKMonitorNode(CreateMode createMode, D data, String path, NodeDataFormatter formatter) {
        super();
        this.createMode = createMode;
        this.data = data;
        this.path = path;
        this.formatter = formatter;
    }

    public boolean existData() {
        return this.data != null;
    }

    public String getPath() {
        return this.path;
    }

    public CreateMode getCreateMode() {
        return this.createMode;
    }

    public SyncStat getSyncStat() {
        return this.syncStat;
    }

    public D getData() {
        return this.data;
    }

    public byte[] getDataBytes() {
        if (this.data == null) {
            return EMPTY_BYTES;
        }
        return formatter.data2Bytes(this.data);
    }

    public Stat getKeeperStat() {
        return keeperStat;
    }

    protected void change(D data) {
        stateUpdater.set(this, SyncStat.NEW);
        if (data != null) {
            this.data = data;
        }
    }

    protected void syncFail() {
        stateUpdater.compareAndSet(this, SyncStat.SYNCING, SyncStat.NEW);
    }

    protected boolean sync() {
        return stateUpdater.compareAndSet(this, SyncStat.NEW, SyncStat.SYNCING);
    }

    protected boolean finish() {
        return stateUpdater.compareAndSet(this, SyncStat.SYNCING, SyncStat.SYNCED);
    }

    protected void setKeeperStat(Stat stat) {
        this.keeperStat = stat;
    }

}
