package com.tny.game.zookeeper;

import com.tny.game.common.thread.CoreThreadFactory;
import com.tny.game.zookeeper.retry.UntilSuccRetryPolicy;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.AsyncCallback.Children2Callback;
import org.apache.zookeeper.AsyncCallback.ChildrenCallback;
import org.apache.zookeeper.AsyncCallback.DataCallback;
import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ZKMonitorClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZKMonitorClient.class);

    private ZKClient keeper;

    protected ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1, new CoreThreadFactory("GameKeeperMonitor-retrey-thread-"));

    public ZKMonitorClient(ZKClient keeper) {
        super();
        this.keeper = keeper;
    }

    public enum MonitorOperation {

        STAT(EnumSet.of(Code.OK, Code.NONODE)) {
            @Override
            public void handleCallBack(AsyncCallback callback, int rc, String path, Object value, Object ctx, Stat stat) {
                ((StatCallback) callback).processResult(rc, path, ctx, stat);
            }

            @Override
            public void addWatcher(ZKClient keeper, String path, Watcher watcher, AsyncCallback cb, Object ctx) {
                keeper.exists(path, watcher, (StatCallback) cb, ctx);
            }
        },

        CHILDREN(EnumSet.of(Code.OK)) {
            @Override
            @SuppressWarnings("unchecked")
            public void handleCallBack(AsyncCallback callback, int rc, String path, Object value, Object ctx, Stat stat) {
                ((ChildrenCallback) callback).processResult(rc, path, ctx, (List<String>) value);
            }

            @Override
            public void addWatcher(ZKClient keeper, String path, Watcher watcher, AsyncCallback cb, Object ctx) {
                keeper.getChildren(path, watcher, (ChildrenCallback) cb, ctx);
            }

        },

        CHILDREN2(EnumSet.of(Code.OK)) {
            @Override
            @SuppressWarnings("unchecked")
            public void handleCallBack(AsyncCallback callback, int rc, String path, Object value, Object ctx, Stat stat) {
                ((Children2Callback) callback).processResult(rc, path, ctx, (List<String>) value, stat);
            }

            @Override
            public void addWatcher(ZKClient keeper, String path, Watcher watcher, AsyncCallback cb, Object ctx) {
                keeper.getChildren2(path, watcher, (Children2Callback) cb, ctx);
            }

        },

        DATA(EnumSet.of(Code.OK)) {
            @Override
            public void handleCallBack(AsyncCallback callback, int rc, String path, Object value, Object ctx, Stat stat) {
                ((DataCallback) callback).processResult(rc, path, ctx, (byte[]) value, stat);
            }

            @Override
            public void addWatcher(ZKClient keeper, String path, Watcher watcher, AsyncCallback cb, Object ctx) {
                keeper.getData(path, watcher, (DataCallback) cb, ctx);
            }
        };

        private final Set<KeeperException.Code> ADD_WATCHER_SUCC_SET;

        MonitorOperation(Set<Code> ADD_WATCHER_SUCC_SET) {
            this.ADD_WATCHER_SUCC_SET = Collections.unmodifiableSet(ADD_WATCHER_SUCC_SET);
        }

        public abstract void handleCallBack(AsyncCallback callback, int rc, String path, Object value, Object ctx, Stat stat);

        public abstract void addWatcher(ZKClient keeper, String path, Watcher watcher, AsyncCallback cb, Object ctx);

    }

    private class MonitorWatcher implements Watcher, Runnable, MonitorTask, StatCallback, DataCallback, ChildrenCallback, Children2Callback {

        protected Watcher handler;

        protected RetryPolicy policy;

        private AsyncCallback callback;

        private String path;

        protected volatile MonitorState state = MonitorState.UNWATCH;

        protected final MonitorOperation operation;

        protected final Object ctx;

        protected AtomicInteger time = new AtomicInteger(0);

        protected volatile boolean working = true;

        protected volatile boolean start = false;

        private MonitorWatcher(String path, MonitorOperation operation, Watcher handler, RetryPolicy policy, AsyncCallback callback, Object ctx) {
            super();
            this.handler = handler;
            this.callback = callback;
            this.operation = operation;
            this.ctx = ctx;
            this.path = path;
            this.policy = policy == null ? new UntilSuccRetryPolicy(3000) : policy;
        }

        private void checkAndDoCallback(int rc, String path, Object ctx, Object value, Stat stat) {
            Code code = KeeperException.Code.get(rc);
            LOGGER.debug("checkAndDoCallback : {}", code);
            if (this.callback != null)
                this.operation.handleCallBack(this.callback, rc, path, value, ctx, stat);
            if (this.operation.ADD_WATCHER_SUCC_SET.contains(code)) {
                this.policy.success();
                this.setState(MonitorState.WATCHING);
                LOGGER.debug("{} 第{}次尝试监听 {} 变化成功 {}, monitor状态:{}", this.path, this.time.get(), this.operation, code, this.state);
            } else if (rc == KeeperException.Code.CONNECTIONLOSS.intValue()) {
                MonitorState state = this.state;
                if (state == MonitorState.UNWATCH)
                    this.doRetry(code);
            } else {
                this.doRetry(code);
            }
        }

        private void doRetry(Code code) {
            this.policy.fail(code);
            LOGGER.debug("{} 第{}次尝试监听 {} 变化由于原因{}失败, monitor状态:{}", this.path, this.time.get(), this.operation, code, this.state);
            this.retry();
        }

        protected boolean retry() {
            if (this.policy.hasNext() && this.working) {
                ZKMonitorClient.this.executorService.schedule(this, this.policy.getDelayTime(), TimeUnit.MILLISECONDS);
                return true;
            }
            return false;
        }

        @Override
        public void run() {
            LOGGER.debug("{} 第{}次尝试监听 {} 变化, monitor状态:{}", this.path, this.time.incrementAndGet(), this.operation, this.state);
            this.operation.addWatcher(ZKMonitorClient.this.keeper, this.path, this, this, this.ctx);
        }

        @Override
        public void start() {
            if (start)
                return;
            synchronized (this) {
                if (start)
                    return;
                LOGGER.debug("{} 第{}次尝试监听 {} 变化, monitor状态:{}", this.path, this.time.incrementAndGet(), this.operation, this.state);
                this.operation.addWatcher(ZKMonitorClient.this.keeper, this.path, this, this, this.ctx);
                this.start = true;
            }
        }

        protected void setState(MonitorState state) {
            this.state = state;
        }

        @Override
        public void process(WatchedEvent event) {
            KeeperState state = event.getState();
            if (state == KeeperState.SyncConnected || state == KeeperState.Expired) {
                this.setState(MonitorState.UNWATCH);
                this.policy.reset();
                LOGGER.debug("{} 第{}次监听到 {} 发生变化, monitor状态:{}, keeperState: {}", this.path, this.time.get(), this.operation, this.state, state);
                if (this.handler != null)
                    this.handler.process(event);
                if (this.working) {
                    this.run();
                }
            } else {
                if (this.handler != null)
                    this.handler.process(event);
            }
        }

        @Override
        public void processResult(int rc, String path, Object ctx, Stat stat) {
            this.checkAndDoCallback(rc, path, ctx, null, stat);
        }

        @Override
        public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
            this.checkAndDoCallback(rc, path, ctx, data, stat);
        }

        @Override
        public void processResult(int rc, String path, Object ctx, List<String> children) {
            this.checkAndDoCallback(rc, path, ctx, children, null);
        }

        @Override
        public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {
            this.checkAndDoCallback(rc, path, ctx, children, stat);
        }

        @Override
        public RetryPolicy getPolicy() {
            return this.policy;
        }

        @Override
        public String getPath() {
            return this.path;
        }

        @Override
        public MonitorState getState() {
            return this.state;
        }

        @Override
        public MonitorOperation getOperation() {
            return this.operation;
        }

        @Override
        public void cancel() {
            this.working = false;
        }

        @Override
        public boolean isWorking() {
            return this.working;
        }

    }

    public MonitorTask monitorExists(String path, Watcher handler, StatCallback cb, Object ctx) {
        return this.monitorExists(path, handler, cb, ctx, null);
    }

    public MonitorTask monitorExists(String path, Watcher handler, StatCallback cb, Object ctx, RetryPolicy policy) {
        MonitorWatcher watcher = new MonitorWatcher(path, MonitorOperation.STAT, handler, policy, cb, ctx);
        this.keeper.exists(path, watcher, watcher, ctx);
        return watcher;
    }

    public MonitorTask monitorData(String path, Watcher handler, DataCallback cb, Object ctx) {
        return this.monitorData(path, handler, cb, ctx, null);
    }

    public MonitorTask monitorData(String path, Watcher handler, DataCallback cb, Object ctx, RetryPolicy policy) {
        MonitorWatcher watcher = new MonitorWatcher(path, MonitorOperation.DATA, handler, policy, cb, ctx);
        return watcher;
    }

    public MonitorTask monitorChildren(String path, Watcher handler, ChildrenCallback cb, Object ctx) {
        return this.monitorChildren(path, handler, cb, ctx, null);
    }

    public MonitorTask monitorChildren(String path, Watcher handler, ChildrenCallback cb, Object ctx, RetryPolicy policy) {
        MonitorWatcher watcher = new MonitorWatcher(path, MonitorOperation.CHILDREN, handler, policy, cb, ctx);
        return watcher;
    }

    public MonitorTask monitorChildren2(String path, Watcher handler, Children2Callback cb, Object ctx) {
        return this.monitorChildren2(path, handler, cb, ctx, null);
    }

    public MonitorTask monitorChildren2(String path, Watcher handler, Children2Callback cb, Object ctx, RetryPolicy policy) {
        MonitorWatcher watcher = new MonitorWatcher(path, MonitorOperation.CHILDREN2, handler, policy, cb, ctx);
        return watcher;
    }

}
