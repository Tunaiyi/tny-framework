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
import com.tny.game.codec.jackson.*;
import com.tny.game.common.concurrent.*;
import com.tny.game.common.event.*;
import com.tny.game.common.event.notifier.*;
import com.tny.game.common.notifier.*;
import com.tny.game.common.utils.*;
import com.tny.game.namespace.*;
import com.tny.game.namespace.exception.*;
import com.tny.game.namespace.listener.*;
import com.tny.game.namespace.sharding.*;
import com.tny.game.namespace.sharding.listener.*;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.Function;
import java.util.stream.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;
import static org.slf4j.LoggerFactory.*;

/**
 * Etcd 基础 Hash 集群
 * <p>
 *
 * @author kgtny
 * @date 2022/7/7 04:04
 **/
public abstract class EtcdNodeHashing<N extends ShardingNode> extends EtcdObject implements NodeHashing<N> {

    private static final int REGISTER_RESULT_SUCCESS = 1;

    private static final int REGISTER_RESULT_FAILED = 2;

    private static final int REGISTER_RESULT_RETRY = 3;

    private static final int REGISTER_RESULT_CANCEL = 4;

    private static final AtomicInteger INDEX = new AtomicInteger();

    private static final ScheduledExecutorService SCHEDULED = Executors.newScheduledThreadPool(1, new CoreThreadFactory("EtcdHashRingScheduled"));

    private static final Logger LOGGER = getLogger(EtcdNodeHashing.class);

    private final EventNotifier<ShardingListener<N>, Sharding<N>> event = EventNotifiers.notifier(ShardingListener.class);

    private static final int INIT = 0;

    private static final int STARTING = 1;

    private static final int EXECUTE = 2;

    private static final int CLOSED = 3;

    private final NamespaceExplorer explorer;

    // 名字
    private final String name;

    // 分区路径
    private final String path;

    private final long ttl;

    // hash 算法
    private final Hasher<String> keyHasher;

    // hash 算法
    private final Hasher<PartitionSlot<N>> nodeHasher;

    // 序列化类型
    private final ObjectMimeType<PartitionSlot<N>> partitionMineType;

    // 分区恢复状态
    private final AtomicBoolean restoring = new AtomicBoolean();

    // 租客
    private volatile Lessee lessee;

    // 分区监控器
    private volatile NameNodesWatcher<PartitionSlot<N>> partitionWatcher;

    // 本地节点
    private final Map<String, List<PartitionRegisterTask>> nodePartitionTaskMap = new ConcurrentHashMap<>();

    // 状态
    private volatile int status = INIT;

    // 每一节点分区数
    private final int partitionCount;

    private final boolean enableRehash;

    private final long maxSlots;

    private final WatchListener<PartitionSlot<N>> partitionListener = new WatchListener<>() {

        @Override
        public void onLoad(NameNodesWatcher<PartitionSlot<N>> watcher, List<NameNode<PartitionSlot<N>>> nameNodes) {
            loadPartitions(nameNodes.stream().map(NameNode::getValue).collect(Collectors.toList()));
        }

        @Override
        public void onCreate(NameNodesWatcher<PartitionSlot<N>> watcher, NameNode<PartitionSlot<N>> node) {
            putPartition(node.getValue());
        }

        @Override
        public void onUpdate(NameNodesWatcher<PartitionSlot<N>> watcher, NameNode<PartitionSlot<N>> node) {
            putPartition(node.getValue());
        }

        @Override
        public void onDelete(NameNodesWatcher<PartitionSlot<N>> watcher, NameNode<PartitionSlot<N>> node) {
            var removeNode = node.getValue();
            var tasks = nodePartitionTaskMap.get(removeNode.getNode().getKey());
            if (tasks != null) {
                for (var task : tasks) {
                    if (task.partition.getSlot() == removeNode.getSlot()) {
                        task.register();
                    }
                }
            }
            removePartition(node.getValue());
        }

    };

    private final LesseeListener lesseeListener = new LesseeListener() {

        @Override
        public void onLease(Lessee source) {
            restorePartition();
        }

        @Override
        public void onResume(Lessee source) {
            restorePartition();
        }

    };

    protected EtcdNodeHashing(String path, HashingOptions<N> option, NamespaceExplorer explorer,
            ObjectCodecAdapter objectCodecAdapter, boolean enableRehash) {
        super(objectCodecAdapter, ifNull(option.getCharset(), NamespaceConstants.CHARSET));
        this.name = ifNotBlank(option.getName(), () -> "HashingRing-" + INDEX.incrementAndGet());
        this.explorer = explorer;
        this.ttl = option.getTtl();
        this.partitionCount = Math.max(1, option.getPartitionCount());
        this.path = NamespacePathNames.dirPath(path);
        this.keyHasher = Objects.requireNonNull(option.getKeyHasher(), "key hasher is null");
        this.nodeHasher = Objects.requireNonNull(option.getNodeHasher(), "node hasher is null");
        this.enableRehash = enableRehash;
        this.maxSlots = Math.max(1, option.getMaxSlots());
        Asserts.checkArgument(this.partitionCount <= this.maxSlots,
                "partitionCount {} must less or equals than maxSlots {}.", this.partitionCount, this.maxSlots);
        this.partitionMineType = ObjectMimeType.of(option.getType(), JsonMimeType.JSON);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getMaxSlots() {
        return maxSlots;
    }

    @Override
    public String getPath() {
        return path;
    }

    protected void fireChange(Sharding<N> sharding, List<Partition<N>> partitions) {
        event.notify(ShardingListener::onChange, sharding, partitions);
    }

    protected void fireRemove(Sharding<N> sharding, List<Partition<N>> partitions) {
        event.notify(ShardingListener::onRemove, sharding, partitions);
    }

    @Override
    public CompletableFuture<NodeHashing<N>> start() {
        synchronized (this) {
            CompletableFuture<NodeHashing<N>> future = new CompletableFuture<>();
            if (status == INIT) {
                status = STARTING;
                if (this.lessee == null) {
                    this.doLeaseAndWatch(future);
                } else {
                    this.doWatch(future);
                }
                future.whenComplete((r, cause) -> {
                    if (cause != null) {
                        onStartFailed();
                    }
                });
                return future;
            }
            future.completeExceptionally(new NamespaceHashingException("{} start failed, status is {}", this.name, this.status));
            return future;
        }
    }

    @Override
    public EventWatchAdapter<ShardingListener<N>> event() {
        return event;
    }

    public Hasher<String> getKeyHasher() {
        return keyHasher;
    }

    public Hasher<PartitionSlot<N>> getNodeHasher() {
        return nodeHasher;
    }

    private void onStartFailed() {
        if (status != STARTING) {
            return;
        }
        synchronized (this) {
            if (status != STARTING) {
                return;
            }
            status = INIT;
        }
    }

    /**
     * 注册节点
     *
     * @param node 节点
     * @return 返回注册的分区
     */
    @Override
    public CompletableFuture<List<Partition<N>>> register(N node) {
        return registerNode(node, (n) -> createPartitionTasks(createPartitionSlots(n), true));
    }

    @Override
    public CompletableFuture<List<Partition<N>>> register(N node, Set<Long> slotIndexes) {
        return registerNode(node, (n) -> createPartitionTasks(createPartitionSlots(n, slotIndexes), false));
    }

    /**
     * 关闭
     */
    @Override
    public void shutdown() {
        if (this.status == CLOSED) {
            return;
        }
        synchronized (this) {
            if (this.status == CLOSED) {
                return;
            }
            if (this.partitionWatcher != null) {
                this.partitionWatcher.unwatch();
                this.partitionWatcher = null;
            }
            doShutdown();
            nodePartitionTaskMap.forEach((id, taskMap) -> taskMap.forEach(PartitionRegisterTask::close));
            nodePartitionTaskMap.clear();
            if (this.lessee != null) {
                this.lessee.shutdown();
                this.lessee = null;
            }
            this.status = CLOSED;
        }
    }

    private CompletableFuture<List<Partition<N>>> registerNode(N node, Function<N, List<PartitionRegisterTask>> tasksFactory) {
        if (this.status != EXECUTE) {
            return CompleteFutureAide.failedFuture(new NamespaceHashingException("ring is not start, status is {}", this.status));
        }
        synchronized (this) {
            if (this.status != EXECUTE) {
                return CompleteFutureAide.failedFuture(new NamespaceHashingException("ring is not start, status is {}", this.status));
            }
            if (nodePartitionTaskMap.containsKey(node.getKey())) {
                return CompleteFutureAide.failedFuture(new NamespaceHashingPartitionException("register failed"));
            }
            var partitionTasks = tasksFactory.apply(node);
            if (nodePartitionTaskMap.putIfAbsent(node.getKey(), partitionTasks) != null) {
                return CompleteFutureAide.failedFuture(new NamespaceHashingPartitionException("register failed"));
            }
            return registerPartitionTasks(partitionTasks);
        }
    }

    private CompletableFuture<List<Partition<N>>> registerPartitionTasks(List<PartitionRegisterTask> tasks) {
        CompletableFuture<List<Partition<N>>> finalFuture = new CompletableFuture<>();
        List<CompletableFuture<ShardingPartition<N>>> futures = Collections.synchronizedList(new ArrayList<>());
        List<Partition<N>> successList = Collections.synchronizedList(new ArrayList<>());
        for (var task : tasks) {
            CompletableFuture<ShardingPartition<N>> future = task.register(); // 任务注册 Future
            future.whenComplete((partition, cause) -> {
                if (partition != null) {
                    successList.add(partition);
                }
                // 无论成功失败都移除
                futures.remove(future);
                if (futures.isEmpty()) {
                    finalFuture.complete(successList);
                }
            });
        }
        return finalFuture;
    }

    private List<PartitionSlot<N>> createPartitionSlots(N node) {
        var slotSet = new ArrayList<PartitionSlot<N>>();
        long maxSlots = this.getMaxSlots();
        long count = Math.min(maxSlots, this.partitionCount);
        for (int index = 0; slotSet.size() < count; index++) {
            var slot = new PartitionSlot<>(index, node);
            slot.hash(nodeHasher, maxSlots);
            slotSet.add(slot);
        }
        return slotSet;
    }

    private List<PartitionSlot<N>> createPartitionSlots(N node, Set<Long> slotIndexes) {
        var slotSet = new ArrayList<PartitionSlot<N>>();
        int index = 0;
        for (Long slotInde : slotIndexes) {
            var slot = new PartitionSlot<>(index++, node, slotInde);
            slotSet.add(slot);
        }
        return slotSet;
    }

    private List<PartitionRegisterTask> createPartitionTasks(List<PartitionSlot<N>> slots, boolean rehash) {
        List<PartitionRegisterTask> tasks = new ArrayList<>();
        for (var partitionedNode : slots) {
            tasks.add(new PartitionRegisterTask(partitionedNode, enableRehash && rehash));
        }
        return tasks;
    }

    private void doWatch(CompletableFuture<NodeHashing<N>> future) {
        if (partitionWatcher != null) {
            future.complete(this);
            return;
        }
        partitionWatcher = explorer.allNodeWatcher(path, partitionMineType);
        partitionWatcher.addListener(partitionListener);
        partitionWatcher.watch().whenComplete((watcher, cause) -> {
            if (cause != null) {
                LOGGER.error("{} lease exception", this.name, cause);
                future.completeExceptionally(cause);
            } else {
                doExecute(watcher);
                future.complete(this);
            }
        });
    }

    private void doLeaseAndWatch(CompletableFuture<NodeHashing<N>> future) {
        if (lessee != null) {
            return;
        }
        explorer.lease(name, this.ttl).whenComplete((lessee, cause) -> {
            if (cause != null) {
                LOGGER.error("{} lease exception ", this.name, cause);
                future.completeExceptionally(cause);
            } else {
                this.setLessee(lessee);
                this.doWatch(future);
            }
        });
    }

    private void doExecute(NameNodesWatcher<PartitionSlot<N>> watcher) {
        synchronized (this) {
            if (status == STARTING) {
                this.partitionWatcher = watcher;
                this.status = EXECUTE;
            } else {
                watcher.unwatch();
            }
        }
    }

    protected abstract void loadPartitions(List<Partition<N>> partitions);

    protected abstract void putPartition(Partition<N> partition);

    protected abstract void removePartition(Partition<N> partition);

    protected abstract void doShutdown();

    private void setLessee(Lessee lessee) {
        synchronized (this) {
            Lessee old = this.lessee;
            if (old != null) {
                old.shutdown();
            }
            this.lessee = lessee;
            this.lessee.event().add(lesseeListener);
        }
    }

    private void retry(Runnable runnable, long delay) {
        SCHEDULED.schedule(runnable, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * 键值 Hash 值
     *
     * @param key 键值
     * @return 返回键值 hash 值
     */
    protected long keyHash(String key) {
        return Math.abs(keyHasher.hash(key, 0, maxSlots));
    }

    /**
     * 节点hash 值
     *
     * @param slot  节点
     * @param index 第几分区
     * @return 返回 hash 值
     */
    private long slotHash(PartitionSlot<N> slot, int index) {
        return Math.abs(nodeHasher.hash(slot, index, this.maxSlots));
    }

    protected String partitionPath(String slotPath, Partition<N> partition) {
        return slotPath;
    }

    private void restorePartition() {
        if (restoring.compareAndSet(false, true)) {
            synchronized (this) {
                allPartitionTaskStream().forEach(PartitionRegisterTask::register);
            }
            restoring.set(false);
        }
    }

    private Stream<PartitionRegisterTask> allPartitionTaskStream() {
        return nodePartitionTaskMap.values().stream().flatMap(Collection::stream);
    }

    private void registerPartition(PartitionRegisterTask task, CompletableFuture<ShardingPartition<N>> future) {
        PartitionSlot<N> partition = task.getPartition();
        if (lessee.isLive()) {
            var slotPath = NamespacePathNames.nodePath(path, NumberFormatAide.alignDigits(partition.getSlot(), this.maxSlots));
            explorer.add(partitionPath(slotPath, partition), partitionMineType, partition, lessee)
                    .whenComplete((nameNode, cause) -> {
                        if (nameNode != null) {
                            task.onCompleted(future, REGISTER_RESULT_SUCCESS);
                        } else {
                            if (cause != null) {
                                task.onCompleted(future, REGISTER_RESULT_RETRY);
                                LOGGER.error("registerPartition {} failed", partition, cause);
                            } else {
                                task.onCompleted(future, REGISTER_RESULT_FAILED);
                            }
                        }
                    });
        } else {
            if (lessee.isShutdown()) {
                task.onCompleted(future, REGISTER_RESULT_CANCEL);
            } else {
                task.onCompleted(future, REGISTER_RESULT_RETRY);
            }
        }
    }

    protected class PartitionRegisterTask {

        private final PartitionSlot<N> partition;

        private volatile boolean closed = false;

        private volatile boolean sync = false;

        private final boolean rehash;

        private volatile CompletableFuture<ShardingPartition<N>> future;

        protected PartitionRegisterTask(PartitionSlot<N> partition, boolean rehash) {
            this.partition = partition;
            this.rehash = rehash;
        }

        private PartitionSlot<N> getPartition() {
            return partition;
        }

        protected void close() {
            synchronized (this) {
                this.closed = true;
                if (this.future != null) {
                    this.future.cancel(true);
                }
            }
        }

        protected CompletableFuture<ShardingPartition<N>> register() {
            synchronized (this) {
                if (closed) {
                    if (this.future != null) {
                        return future;
                    } else {
                        var future = new CompletableFuture<ShardingPartition<N>>();
                        future.cancel(true);
                        return future;
                    }
                }
                if (sync && future != null) {
                    return future;
                }
                sync = true;
                if (this.future == null) {
                    this.future = new CompletableFuture<>();
                }
                try {
                    registerPartition(this, future);
                } catch (Throwable e) {
                    sync = false;
                }
                return future;
            }
        }

        private void tryAgain(CompletableFuture<ShardingPartition<N>> future) {
            synchronized (this) {
                if (closed && sync || future.isDone()) {
                    return;
                }
                try {
                    sync = true;
                    if (rehash) { // 需要rehash
                        this.partition.hash(nodeHasher, maxSlots);
                    }
                    registerPartition(this, future);
                } catch (Throwable e) {
                    sync = false;
                }
            }
        }

        private void retry(CompletableFuture<ShardingPartition<N>> future) {
            EtcdNodeHashing.this.retry(() -> this.tryAgain(future), 5000);
        }

        private void onCompleted(CompletableFuture<ShardingPartition<N>> future, int result) {
            synchronized (this) {
                try {
                    if (result == REGISTER_RESULT_SUCCESS) { // 成功
                        future.complete(partition);
                        return;
                    }
                    if (result == REGISTER_RESULT_CANCEL) { // 取消
                        future.cancel(true);
                        return;
                    }
                    if (result == REGISTER_RESULT_RETRY) { // 异常失败
                        retry(future);
                        return;
                    }
                    if (result == REGISTER_RESULT_FAILED) { // 插入失败
                        if (rehash) {
                            retry(future);
                        } else {
                            future.complete(null);
                        }
                    }
                } finally {
                    sync = false;
                    if (future.isDone() && this.future == future) {
                        this.future = null;
                    }
                }
            }
        }

    }

}


