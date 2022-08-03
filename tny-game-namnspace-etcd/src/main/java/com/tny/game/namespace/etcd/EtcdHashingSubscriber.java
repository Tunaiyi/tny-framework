package com.tny.game.namespace.etcd;

import com.google.common.collect.ImmutableMap;
import com.tny.game.codec.*;
import com.tny.game.common.*;
import com.tny.game.namespace.*;
import com.tny.game.namespace.consistenthash.*;
import com.tny.game.namespace.exception.*;
import com.tny.game.namespace.listener.*;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.stream.*;

/**
 * 哈希节点订阅器
 * <p>
 *
 * @author kgtny
 * @date 2022/7/8 19:50
 **/
public class EtcdHashingSubscriber<T> extends EtcdHashing<T> implements HashingSubscriber<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EtcdHashingSubscriber.class);

    private static final long WATCH_ALL_SLOT = -1L;

    private volatile Map<Long, RangeWatcher<T>> watcherMap = ImmutableMap.of();

    private final long maxSlotSize;

    private final List<WatchListener<T>> watchListeners = new CopyOnWriteArrayList<>();

    private boolean close = false;

    private final WatchListener<T> notifier = new WatchListener<>() {

        @Override
        public void onLoad(NameNodesWatcher<T> watcher, List<NameNode<T>> nameNodes) {
            fire(l -> l.onLoad(watcher, nameNodes));
        }

        @Override
        public void onCreate(NameNodesWatcher<T> watcher, NameNode<T> node) {
            fire(l -> l.onCreate(watcher, node));
        }

        @Override
        public void onUpdate(NameNodesWatcher<T> watcher, NameNode<T> node) {
            fire(l -> l.onUpdate(watcher, node));
        }

        @Override
        public void onDelete(NameNodesWatcher<T> watcher, NameNode<T> node) {
            fire(l -> l.onDelete(watcher, node));
        }

    };

    public EtcdHashingSubscriber(String rootPath, long maxSlotSize, ObjectMineType<T> mineType, NamespaceExplorer explorer) {
        super(rootPath, mineType, explorer);
        this.maxSlotSize = maxSlotSize;
    }

    @Override
    protected long getMaxSlots() {
        return this.maxSlotSize;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public void subscribeAll() {
        if (close) {
            return;
        }
        synchronized (this) {
            if (close) {
                return;
            }
            var existMap = new HashMap<>(this.watcherMap);
            Map<Long, RangeWatcher<T>> watchers;
            var exist = existMap.remove(WATCH_ALL_SLOT);
            if (exist == null) {
                watchers = addAllSubscription();
            } else {
                watchers = Map.of(WATCH_ALL_SLOT, exist);
            }
            existMap.forEach((slot, sub) -> sub.close());
            this.watcherMap = watchers;
        }
    }

    @Override
    public void subscribe(List<? extends ShardingRange<?>> ranges) {
        if (close) {
            return;
        }
        synchronized (this) {
            if (close) {
                return;
            }
            var existMap = new HashMap<>(this.watcherMap);
            var watchers = new HashMap<Long, RangeWatcher<T>>();
            for (var range : ranges) {
                var exist = existMap.remove(range.getToSlot());
                if (exist == null) {
                    addSubscription(range, watchers);
                } else {
                    if (range.getFromSlot() == exist.range.getFromSlot()) {
                        watchers.put(range.getToSlot(), exist);
                    } else {
                        exist.close();
                        addSubscription(range, watchers);
                    }
                }
            }
            existMap.forEach((slot, sub) -> sub.close());
            this.watcherMap = ImmutableMap.copyOf(watchers);
        }
    }

    @Override
    public void unsubscribe() {
        synchronized (this) {
            watcherMap.forEach((slot, sub) -> sub.close());
            watcherMap = ImmutableMap.of();
        }
    }

    @Override
    public void addListener(WatchListener<T> listener) {
        this.watchListeners.add(listener);
    }

    @Override
    public void removeListener(WatchListener<T> listener) {
        this.watchListeners.remove(listener);
    }

    @Override
    public void clearListener() {
        this.watchListeners.clear();
    }

    @Override
    public void close() {
        if (close) {
            return;
        }
        synchronized (this) {
            if (close) {
                return;
            }
            unsubscribe();
            this.close = true;
            this.watchListeners.clear();
        }
    }

    private NamespaceExplorer getExplorer() {
        return explorer;
    }

    private void addSubscription(ShardingRange<?> range, Map<Long, RangeWatcher<T>> map) {
        var newSub = RangeWatcher.ofRange(this, range);
        if (map.putIfAbsent(range.getToSlot(), newSub) == null) {
            newSub.watch();
        }
    }

    private Map<Long, RangeWatcher<T>> addAllSubscription() {
        var newSub = RangeWatcher.ofAll(this);
        newSub.watch();
        return Map.of(WATCH_ALL_SLOT, newSub);
    }

    private void fire(Consumer<WatchListener<T>> handle) {
        for (var listener : watchListeners) {
            try {
                handle.accept(listener);
            } catch (Throwable e) {
                LOGGER.error("", e);
            }
        }
    }

    private String subPath(long slot) {
        return NamespacePathNames.nodePath(path, slotName(slot));
    }

    private ObjectMineType<T> getMineType() {
        return mineType;
    }

    private static class RangeWatcher<T> {

        private final EtcdHashingSubscriber<T> parent;

        private final ShardingRange<?> range;

        private boolean start;

        private List<NameNodesWatcher<T>> watchers;

        public static <T> RangeWatcher<T> ofRange(EtcdHashingSubscriber<T> parent, ShardingRange<?> range) {
            return new RangeWatcher<>(parent, range);
        }

        public static <T> RangeWatcher<T> ofAll(EtcdHashingSubscriber<T> parent) {
            return new RangeWatcher<>(parent, null);
        }

        private RangeWatcher(EtcdHashingSubscriber<T> parent, ShardingRange<?> range) {
            this.parent = parent;
            this.range = range;
        }

        private void watch() {
            if (start) {
                return;
            }
            synchronized (this) {
                if (start) {
                    return;
                }
                start = true;
                Stream<NameNodesWatcher<T>> watcherStream;
                if (range == null) {
                    watcherStream = Stream.generate(() -> parent.getExplorer().allNodeWatcher(parent.getPath(), parent.getMineType()));
                } else {
                    watcherStream = range.getRanges().stream()
                            .map((range) -> {
                                long lower = range.lowerEndpoint();
                                long upper = range.upperEndpoint();
                                if (!range.contains(lower) && lower < upper) {
                                    lower++;
                                }
                                if (!range.contains(upper) && lower < upper) {
                                    upper--;
                                }
                                if (!range.contains(lower) || !range.contains(upper)) {
                                    throw new HashingException("It is illegal to {}({}) to {}() for the interval",
                                            range.lowerEndpoint(), range.lowerBoundType(), range.upperEndpoint(), range.upperBoundType());
                                }
                                if (lower == upper) {
                                    var path = parent.subPath(range.lowerEndpoint());
                                    return parent.getExplorer().nodeWatcher(
                                            path, parent.getMineType());
                                } else {
                                    var from = parent.subPath(lower);
                                    var to = parent.subPath(upper);
                                    return parent.getExplorer().allNodeWatcher(
                                            from,
                                            to,
                                            parent.getMineType());
                                }
                            });
                }
                this.watchers = watcherStream
                        .peek(watcher -> watcher.addListener(parent.notifier))
                        .peek(this::doWatch)
                        .collect(Collectors.toList());
            }
        }

        private void doWatch(NameNodesWatcher<T> watcher) {
            if (watcher.isWatch()) {
                return;
            }
            watcher.watch().whenComplete((w, cause) -> {
                if (cause == null) {
                    return;
                }
                if (watcher.isWatch()) {
                    return;
                }
                CompletableFutureAide.delay(() -> doWatch(watcher), 1000);
            });
        }

        private void close() {
            if (!start) {
                return;
            }
            synchronized (this) {
                if (!start) {
                    return;
                }
                watchers.forEach(NameNodesWatcher::unwatch);
                watchers.clear();
            }
        }

    }

}
