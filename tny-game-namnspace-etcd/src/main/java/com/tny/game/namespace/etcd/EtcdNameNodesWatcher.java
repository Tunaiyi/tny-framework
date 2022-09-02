/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.namespace.etcd;

import com.tny.game.codec.*;
import com.tny.game.common.event.firer.*;
import com.tny.game.namespace.*;
import com.tny.game.namespace.exception.*;
import com.tny.game.namespace.listener.*;
import io.etcd.jetcd.*;
import io.etcd.jetcd.Watch.Watcher;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.*;
import io.etcd.jetcd.watch.*;
import org.slf4j.*;

import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

/**
 * 默认监控对象
 * <p>
 *
 * @author kgtny
 * @date 2022/6/30 23:44
 **/
public class EtcdNameNodesWatcher<T> extends EtcdObject implements NameNodesWatcher<T> {

    private static final int UNWATCH = 0;

    private static final int TRY_WATCH = 1;

    private static final int WATCH = 2;

    private static final Logger LOGGER = LoggerFactory.getLogger(EtcdNameNodesWatcher.class);

    private final String watchPath;

    private final String endPath;

    private final ByteSequence key;

    private final ByteSequence endKey;

    private final boolean match;

    private final KV kv;

    private final Watch watch;

    private final ObjectMimeType<T> valueType;

    private final EventFirer<WatcherListener, NameNodesWatcher<T>> watcherEvent = EventFirers.firer(WatcherListener.class);

    private final EventFirer<WatchLoadListener<T>, NameNodesWatcher<T>> loadEvent = EventFirers.firer(WatchLoadListener.class);

    private final EventFirer<WatchCreateListener<T>, NameNodesWatcher<T>> createEvent = EventFirers.firer(WatchCreateListener.class);

    private final EventFirer<WatchUpdateListener<T>, NameNodesWatcher<T>> updateEvent = EventFirers.firer(WatchUpdateListener.class);

    private final EventFirer<WatchDeleteListener<T>, NameNodesWatcher<T>> deleteEvent = EventFirers.firer(WatchDeleteListener.class);

    private Watcher watcher;

    private volatile int status = UNWATCH;

    public EtcdNameNodesWatcher(String watchPath, boolean match, KV kv, Watch watch,
            ObjectMimeType<T> valueType, ObjectCodecAdapter objectCodecAdapter, Charset charset) {
        this(watchPath, null, match, kv, watch, valueType, objectCodecAdapter, charset);
    }

    public EtcdNameNodesWatcher(String watchPath, String endPath, boolean match, KV kv, Watch watch,
            ObjectMimeType<T> valueType, ObjectCodecAdapter objectCodecAdapter, Charset charset) {
        super(objectCodecAdapter, charset);
        this.watchPath = watchPath;
        this.endPath = endPath;
        this.match = match;
        this.kv = kv;
        this.watch = watch;
        this.valueType = valueType;
        this.key = toBytes(watchPath);
        if (endPath != null) {
            endKey = toBytes(endPath);
        } else {
            endKey = null;
        }
    }

    @Override
    public String getWatchPath() {
        return watchPath;
    }

    @Override
    public boolean isMatch() {
        return match;
    }

    @Override
    public CompletableFuture<NameNodesWatcher<T>> watch() {
        if (status != UNWATCH) {
            return CompletableFuture.failedFuture(new NamespaceWatchException("watch failed when status is " + this.status));
        }
        synchronized (this) {
            if (status != UNWATCH) {
                return CompletableFuture.failedFuture(new NamespaceWatchException("watch failed when status is " + this.status));
            }
            status = TRY_WATCH;
            try {
                GetOption option;
                if (endPath != null) {
                    option = GetOption.newBuilder().withRange(endKey).build();
                } else {
                    option = GetOption.newBuilder().isPrefix(match).build();
                }
                CompletableFuture<NameNodesWatcher<T>> future = new CompletableFuture<>();
                kv.get(key, option)
                        .whenComplete((response, cause) -> {
                            if (cause != null) {
                                doLoadFailed(cause);
                                future.completeExceptionally(cause);
                            } else {
                                doWatch(response, future);
                            }
                        });
                return future;
            } catch (Throwable e) {
                LOGGER.error("watch {} exception", watchPath, e);
                status = UNWATCH;
                throw e;
            }
        }
    }

    @Override
    public synchronized void unwatch() {
        if (status == UNWATCH) {
            return;
        }
        closeWatcher();
    }

    @Override
    public boolean isUnwatch() {
        return status == UNWATCH;
    }

    @Override
    public boolean isWatch() {
        return status == WATCH;
    }

    @Override
    public EventSource<WatcherListener> watcherEvent() {
        return watcherEvent;
    }

    @Override
    public EventSource<WatchLoadListener<T>> loadEvent() {
        return loadEvent;
    }

    @Override
    public EventSource<WatchCreateListener<T>> createEvent() {
        return createEvent;
    }

    @Override
    public EventSource<WatchUpdateListener<T>> updateEvent() {
        return updateEvent;
    }

    @Override
    public EventSource<WatchDeleteListener<T>> deleteEvent() {
        return deleteEvent;
    }

    @Override
    public void addListener(WatchListener<T> listener) {
        loadEvent.add(listener);
        createEvent.add(listener);
        updateEvent.add(listener);
        deleteEvent.add(listener);
    }

    @Override
    public void removeListener(WatchListener<T> listener) {
        loadEvent.remove(listener);
        createEvent.remove(listener);
        updateEvent.remove(listener);
        deleteEvent.remove(listener);
    }

    private synchronized void doLoadFailed(Throwable cause) {
        LOGGER.error("load {} exception", watchPath, cause);
        if (this.status == TRY_WATCH) {
            this.status = UNWATCH;
        }
        throw new NamespaceWatchException(format("load {} exception", watchPath, status), cause);
    }

    private synchronized void doWatch(GetResponse response, CompletableFuture<NameNodesWatcher<T>> future) {
        try {
            if (status != TRY_WATCH) {
                throw new NamespaceWatchException(format("watch {} failed when status is {}", watchPath, status));
            }
            long watchRevision = response.getHeader().getRevision();
            List<NameNode<T>> nodes = as(decodeAllKeyValues(response.getKvs(), valueType));
            this.loadEvent.fire(WatchLoadListener::onLoad, this, nodes);
            WatchOption.Builder optionBuilder = WatchOption.newBuilder()
                    .withRevision(watchRevision + 1)
                    .withPrevKV(true)
                    .isPrefix(match)
                    .withProgressNotify(true);
            if (endKey != null) {
                optionBuilder.withRange(endKey).isPrefix(false);
            }
            this.watcher = watch.watch(key, optionBuilder.build(), etcdWatchListener);

            status = WATCH;
            future.complete(this);
        } catch (Throwable e) {
            status = UNWATCH;
            var cause = new NamespaceWatchException(format("watch {} exception", watchPath), e);
            future.completeExceptionally(cause);
            throw cause;
        }
    }

    private final Watch.Listener etcdWatchListener = new Watch.Listener() {

        @Override
        public void onNext(WatchResponse response) {
            for (WatchEvent event : response.getEvents()) {
                KeyValue kv = event.getKeyValue();
                if (kv.getVersion() == 0) {
                    var removeKv = event.getPrevKV();
                    NameNode<T> node = decode(removeKv.getValue().getBytes(), kv, removeKv.getCreateRevision(), removeKv.getVersion(), valueType);
                    deleteEvent.fire(WatchDeleteListener::onDelete, EtcdNameNodesWatcher.this, node);
                } else {
                    KeyValue preKv = event.getPrevKV();
                    NameNode<T> node = decode(event.getKeyValue(), valueType);
                    if (preKv.getVersion() == 0) {
                        createEvent.fire(WatchCreateListener::onCreate, EtcdNameNodesWatcher.this, node);
                    } else {
                        updateEvent.fire(WatchUpdateListener::onUpdate, EtcdNameNodesWatcher.this, node);
                    }
                }
            }
        }

        @Override
        public void onError(Throwable throwable) {
            onWatchException(throwable);
        }

        @Override
        public void onCompleted() {
            onWatchCompleted();
        }

    };

    private synchronized void onWatchException(Throwable cause) {
        LOGGER.error("watching {} exception", watchPath, cause);
        watcherEvent.fire(WatcherListener::onError, EtcdNameNodesWatcher.this, cause);
    }

    private synchronized void onWatchCompleted() {
        closeWatcher();
        watcherEvent.fire(WatcherListener::onCompleted, EtcdNameNodesWatcher.this);
    }

    private void closeWatcher() {
        if (this.watcher != null) {
            this.watcher.close();
            this.watcher = null;
            status = UNWATCH;
        }
    }

}
