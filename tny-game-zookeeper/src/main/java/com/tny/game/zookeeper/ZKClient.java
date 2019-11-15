package com.tny.game.zookeeper;

import org.apache.zookeeper.AsyncCallback.*;
import org.apache.zookeeper.*;
import org.apache.zookeeper.Watcher.Event.*;
import org.apache.zookeeper.ZooKeeper.States;
import org.apache.zookeeper.data.*;
import org.slf4j.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ZKClient {

    private final Logger LOGGER = LoggerFactory.getLogger(ZKClient.class);

    private ZooKeeper zooKeeper;

    private volatile CountDownLatch latch = new CountDownLatch(1);
    ;

    private AtomicBoolean connected = new AtomicBoolean(false);

    private String connectString;

    private int sessionTimeout;

    private Watcher childWatcher;

    private List<RenewTask> renewTaskList = new CopyOnWriteArrayList<RenewTask>();

    private class ConnectedWatch implements Watcher {

        @Override
        public void process(WatchedEvent event) {
            EventType eventType = event.getType();
            KeeperState state = event.getState();
            try {
                if (eventType == EventType.None) {
                    if (state == KeeperState.Disconnected) {
                        ZKClient.this.connected.set(false);
                    } else if (state == KeeperState.SyncConnected) {
                        ZKClient.this.LOGGER.info("zookeeper 链接成功 {}", state);
                        ZKClient.this.latch.countDown();
                    } else if (state == KeeperState.Expired) {
                        ZKClient.this.LOGGER.warn("keeper state {}", state);
                        ZKClient.this.close();
                        ZKClient.this.start();
                        for (RenewTask task : ZKClient.this.renewTaskList)
                            task.renew(state, ZKClient.this);
                    }
                }
                if (ZKClient.this.childWatcher != null)
                    ZKClient.this.childWatcher.process(event);
            } catch (IOException e) {
                ZKClient.this.LOGGER.error("ConnectedWatch process {} {} IOException ", eventType, state, e);
            } catch (Exception e) {
                ZKClient.this.LOGGER.error("ConnectedWatch process {} {} exception ", eventType, state, e);
            }
        }

    }

    public ZKClient(String connectString, int sessionTimeout, Watcher watcher) {
        this(connectString, sessionTimeout, watcher, null);
    }

    public ZKClient(String connectString, int sessionTimeout, Watcher watcher, RenewTask task) {
        this.connectString = connectString;
        this.sessionTimeout = sessionTimeout;
        this.childWatcher = watcher;
        if (task != null)
            this.renewTaskList.add(task);
    }

    public void addRenewTask(RenewTask task) {
        this.renewTaskList.add(task);
    }

    public void removeRenewTask(RenewTask task) {
        this.renewTaskList.remove(task);
    }

    public void clearRenewTask(RenewTask task) {
        this.renewTaskList.clear();
    }

    public boolean start() throws IOException {
        if (this.connected.compareAndSet(false, true)) {
            if (this.zooKeeper != null) {
                try {
                    this.zooKeeper.close();
                } catch (InterruptedException e) {
                    this.LOGGER.error("connect exception", e);
                }
            }
            this.zooKeeper = new ZooKeeper(this.connectString, this.sessionTimeout, new ConnectedWatch());
            try {
                this.latch.await();
                this.latch = new CountDownLatch(1);
            } catch (InterruptedException e) {
                this.LOGGER.error("connect exception", e);
            }
            return true;
        }
        return false;
    }

    public void close() {
        if (this.zooKeeper != null) {
            try {
                this.LOGGER.info("正在关闭{}", this.zooKeeper);
                if (this.zooKeeper != null)
                    this.zooKeeper.close();
                this.connected.set(false);
                this.zooKeeper = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public long getSessionId() {
        return this.zooKeeper.getSessionId();
    }

    public byte[] getSessionPasswd() {
        return this.zooKeeper.getSessionPasswd();
    }

    public int getSessionTimeout() {
        return this.zooKeeper.getSessionTimeout();
    }

    public void addAuthInfo(String scheme, byte auth[]) {
        this.zooKeeper.addAuthInfo(scheme, auth);
    }

    public synchronized void register(Watcher watcher) {
        this.childWatcher = watcher;
    }

    public String create(final String path, byte data[], List<ACL> acl, CreateMode createMode) throws KeeperException, InterruptedException {
        return this.zooKeeper.create(path, data, acl, createMode);
    }

    public void create(final String path, byte data[], List<ACL> acl, CreateMode createMode, StringCallback cb, Object ctx) {
        this.zooKeeper.create(path, data, acl, createMode, cb, ctx);
    }

    public void delete(final String path, int version) throws InterruptedException, KeeperException {
        this.zooKeeper.delete(path, version);
    }

    public void delete(final String path, int version, VoidCallback cb, Object ctx) {
        this.zooKeeper.delete(path, version, cb, ctx);
    }

    public Stat exists(final String path, Watcher watcher) throws KeeperException, InterruptedException {
        return this.zooKeeper.exists(path, watcher);
    }

    public Stat exists(String path, boolean watch) throws KeeperException, InterruptedException {
        return this.zooKeeper.exists(path, watch);
    }

    public void exists(final String path, Watcher watcher, StatCallback cb, Object ctx) {
        this.zooKeeper.exists(path, watcher, cb, ctx);
    }

    public void exists(String path, boolean watch, StatCallback cb, Object ctx) {
        this.zooKeeper.exists(path, watch, cb, ctx);
    }

    public byte[] getData(final String path, Watcher watcher, Stat stat) throws KeeperException, InterruptedException {
        return this.zooKeeper.getData(path, watcher, stat);
    }

    public byte[] getData(String path, boolean watch, Stat stat) throws KeeperException, InterruptedException {
        return this.zooKeeper.getData(path, watch, stat);
    }

    public void getData(final String path, Watcher watcher, DataCallback cb, Object ctx) {
        this.zooKeeper.getData(path, watcher, cb, ctx);
    }

    public void getData(String path, boolean watch, DataCallback cb, Object ctx) {
        this.zooKeeper.getData(path, watch, cb, ctx);
    }

    public Stat setData(final String path, byte data[], int version) throws KeeperException, InterruptedException {
        return this.zooKeeper.setData(path, data, version);
    }

    public void setData(final String path, byte data[], int version, StatCallback cb, Object ctx) {
        this.zooKeeper.setData(path, data, version, cb, ctx);
    }

    public List<ACL> getACL(final String path, Stat stat) throws KeeperException, InterruptedException {
        return this.zooKeeper.getACL(path, stat);
    }

    public void getACL(final String path, Stat stat, ACLCallback cb, Object ctx) {
        this.zooKeeper.getACL(path, stat, cb, ctx);
    }

    public Stat setACL(final String path, List<ACL> acl, int version) throws KeeperException, InterruptedException {
        return this.zooKeeper.setACL(path, acl, version);
    }

    public void setACL(final String path, List<ACL> acl, int version, StatCallback cb, Object ctx) {
        this.zooKeeper.setACL(path, acl, version, cb, ctx);
    }

    public List<String> getChildren(final String path, Watcher watcher) throws KeeperException, InterruptedException {
        return this.zooKeeper.getChildren(path, watcher);
    }

    public List<String> getChildren(String path, boolean watch) throws KeeperException, InterruptedException {
        return this.zooKeeper.getChildren(path, watch);
    }

    public List<String> getChildren(final String path, Watcher watcher, Stat stat) throws KeeperException, InterruptedException {
        return this.zooKeeper.getChildren(path, watcher, stat);
    }

    public List<String> getChildren(String path, boolean watch, Stat stat) throws KeeperException, InterruptedException {
        return this.zooKeeper.getChildren(path, watch, stat);
    }

    public void getChildren(final String path, Watcher watcher, ChildrenCallback cb, Object ctx) {
        this.zooKeeper.getChildren(path, watcher, cb, ctx);
    }

    public void getChildren(String path, boolean watch, ChildrenCallback cb, Object ctx) {
        this.zooKeeper.getChildren(path, watch, cb, ctx);
    }

    public void getChildren2(final String path, Watcher watcher, Children2Callback cb, Object ctx) {
        this.zooKeeper.getChildren(path, watcher, cb, ctx);
    }

    public void getChildren(String path, boolean watch, Children2Callback cb, Object ctx) {
        this.zooKeeper.getChildren(path, watch, cb, ctx);
    }

    public void sync(final String path, VoidCallback cb, Object ctx) {
        this.zooKeeper.sync(path, cb, ctx);
    }

    public States getState() {
        return this.zooKeeper.getState();
    }

    @Override
    public String toString() {
        return this.zooKeeper.toString();
    }

}
