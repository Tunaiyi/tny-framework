package com.tny.game.namespace.etcd;

import com.google.common.collect.ImmutableMap;
import com.tny.game.codec.*;
import com.tny.game.common.*;
import com.tny.game.namespace.*;
import com.tny.game.namespace.consistenthash.*;
import com.tny.game.namespace.listener.*;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * 哈希节点订阅器
 * <p>
 *
 * @author kgtny
 * @date 2022/7/8 19:50
 **/
public class EtcdHashingSubscriber<T> implements HashingSubscriber<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EtcdHashingSubscriber.class);

    private final NamespaceExplorer explorer;

    private final String path;

    private final HashAlgorithm algorithm;

    private final ObjectMineType<T> mineType;

    private volatile Map<Long, RangeWatcher<T>> watcherMap = ImmutableMap.of();

    private boolean close = false;

    private final List<WatchListener<T>> watchListeners = new CopyOnWriteArrayList<>();

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

    public EtcdHashingSubscriber(String path, ObjectMineType<T> mineType, HashAlgorithm algorithm, NamespaceExplorer explorer) {
        this.path = path;
        this.algorithm = ifNull(algorithm, HashAlgorithms.getDefault());
        this.mineType = mineType;
        this.explorer = explorer;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public void subscribe(List<ShardingRange<?>> ranges) {
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
        var newSub = new RangeWatcher<>(this, this.algorithm, range);
        if (map.putIfAbsent(range.getToSlot(), newSub) == null) {
            newSub.watch();
        }
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
        return NamespacePaths.nodePath(path, this.algorithm.alignDigits(slot));
    }

    private ObjectMineType<T> getMineType() {
        return mineType;
    }

    private static class RangeWatcher<T> {

        private final EtcdHashingSubscriber<T> parent;

        private final ShardingRange<?> range;

        private final HashAlgorithm algorithm;

        private boolean start;

        private List<NameNodesWatcher<T>> watchers;

        private RangeWatcher(EtcdHashingSubscriber<T> parent, HashAlgorithm algorithm, ShardingRange<?> range) {
            this.parent = parent;
            this.algorithm = algorithm;
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
                watchers = range.getRanges().stream()
                        .map((range) -> {
                            var from = parent.subPath(range.lowerEndpoint());
                            var to = parent.subPath(range.upperEndpoint());
                            System.out.println("watch : " + from + " => " + to);
                            return parent.getExplorer().allNodeWatcher(
                                    from,
                                    to,
                                    parent.getMineType());
                        })
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
