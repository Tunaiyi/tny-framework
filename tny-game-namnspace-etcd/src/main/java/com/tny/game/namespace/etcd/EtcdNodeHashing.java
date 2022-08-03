package com.tny.game.namespace.etcd;

import com.tny.game.codec.*;
import com.tny.game.codec.jackson.*;
import com.tny.game.common.concurrent.*;
import com.tny.game.common.event.firer.*;
import com.tny.game.common.utils.*;
import com.tny.game.namespace.*;
import com.tny.game.namespace.consistenthash.*;
import com.tny.game.namespace.consistenthash.listener.*;
import com.tny.game.namespace.exception.*;
import com.tny.game.namespace.listener.*;
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

    private static final AtomicInteger INDEX = new AtomicInteger();

    private static final ScheduledExecutorService SCHEDULED = Executors.newScheduledThreadPool(1, new CoreThreadFactory("EtcdHashRingScheduled"));

    private static final Logger LOGGER = getLogger(EtcdNodeHashing.class);

    private final EventFirer<ShardingListener<N>, Sharding<N>> event = EventFirers.firer(ShardingListener.class);

    private static final int INIT = 0;

    private static final int STARTING = 1;

    private static final int EXECUTE = 2;

    private static final int CLOSE = 3;

    private final NamespaceExplorer explorer;

    // 名字
    private final String name;

    // 分区路径
    private final String path;

    private final long ttl;

    // hash 算法
    private final Hasher<String> keyHash;

    // hash 算法
    private final Hasher<N> nodeHash;

    // 序列化类型
    private final ObjectMineType<NodePartition<N>> partitionMineType;

    // 分区恢复状态
    private final AtomicBoolean restoring = new AtomicBoolean();

    // 租客
    private volatile Lessee lessee;

    // 分区监控器
    private volatile NameNodesWatcher<NodePartition<N>> partitionWatcher;

    // 本地节点
    private final Map<N, List<PartitionTask>> nodePartitionTaskMap = new ConcurrentHashMap<>();

    // 状态
    private volatile int status = INIT;

    // 每一节点分区数
    private final int partitionCount;

    private final boolean enableRehash;

    private final long maxSlots;

    private final WatchListener<NodePartition<N>> partitionListener = new WatchListener<>() {

        @Override
        public void onLoad(NameNodesWatcher<NodePartition<N>> watcher, List<NameNode<NodePartition<N>>> nameNodes) {
            loadPartitions(nameNodes.stream().map(NameNode::getValue).collect(Collectors.toList()));
        }

        @Override
        public void onCreate(NameNodesWatcher<NodePartition<N>> watcher, NameNode<NodePartition<N>> node) {
            putPartition(node.getValue());
        }

        @Override
        public void onUpdate(NameNodesWatcher<NodePartition<N>> watcher, NameNode<NodePartition<N>> node) {
            putPartition(node.getValue());
        }

        @Override
        public void onDelete(NameNodesWatcher<NodePartition<N>> watcher, NameNode<NodePartition<N>> node) {
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
        this.keyHash = Objects.requireNonNull(option.getKeyHasher(), "key hasher is null");
        this.nodeHash = Objects.requireNonNull(option.getNodeHasher(), "node hasher is null");
        this.enableRehash = enableRehash;
        Asserts.checkArgument(keyHash.getMax() == nodeHash.getMax(),
                "keyHash max slots {} are not equals than nodeHash max slots {}.",
                this.keyHash.getMax(), this.nodeHash.getMax());
        this.maxSlots = this.keyHash.getMax();
        this.partitionMineType = ObjectMineType.of(option.getType(), JsonMimeType.JSON);
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
        event.fire(ShardingListener::onChange, sharding, partitions);
    }

    protected void fireRemove(Sharding<N> sharding, List<Partition<N>> partitions) {
        event.fire(ShardingListener::onRemove, sharding, partitions);
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
                future.whenComplete((r, cause) -> onStartFailed());
                return future;
            }
            future.completeExceptionally(new HashRingException(format("{} start failed, status is {}", this.name, this.status)));
            return future;
        }
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

    protected Hasher<String> getKeyHash() {
        return keyHash;
    }

    /**
     * 注册节点
     *
     * @param node 节点
     * @return 返回注册的分区
     */
    @Override
    public CompletableFuture<List<Partition<N>>> register(N node) {
        return registerNode(node, (n) -> createPartitionTasks(n, createSlotSet(node), true));
    }

    @Override
    public CompletableFuture<List<Partition<N>>> register(N node, Set<Long> slotIndexes) {
        return registerNode(node, (n) -> createPartitionTasks(n, slotIndexes, false));
    }

    private CompletableFuture<List<Partition<N>>> registerNode(N node, Function<N, List<PartitionTask>> tasksFactory) {
        if (this.status != EXECUTE) {
            return CompleteFutureAide.failedFuture(new HashRingException("ring is not start, status is {}", this.status));
        }
        synchronized (this) {
            if (this.status != EXECUTE) {
                return CompleteFutureAide.failedFuture(new HashRingException("ring is not start, status is {}", this.status));
            }
            if (nodePartitionTaskMap.containsKey(node)) {
                return CompleteFutureAide.failedFuture(new HashPartitionRegisteredException("register failed"));
            }
            var partitionTasks = tasksFactory.apply(node);
            if (nodePartitionTaskMap.putIfAbsent(node, partitionTasks) != null) {
                return CompleteFutureAide.failedFuture(new HashPartitionRegisteredException("register failed"));
            }
            return registerPartitionTasks(partitionTasks);
        }
    }

    private CompletableFuture<List<Partition<N>>> registerPartitionTasks(List<PartitionTask> tasks) {
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

    private Set<Long> createSlotSet(N node) {
        var slotSet = new TreeSet<Long>();
        for (int index = 0; slotSet.size() < this.partitionCount; index++) {
            slotSet.add(this.nodeHash(node, index));
        }
        return slotSet;
    }

    @Override
    public EventSource<ShardingListener<N>> event() {
        return event;
    }

    private List<PartitionTask> createPartitionTasks(N node, Set<Long> slotIndexes, boolean rehash) {
        var index = 0;
        List<PartitionTask> tasks = new ArrayList<>();
        for (var slotIndex : slotIndexes) {
            tasks.add(new PartitionTask(index++, node, slotIndex, enableRehash && rehash));
        }
        return tasks;
    }

    /**
     * 关闭
     */
    @Override
    public void shutdown() {
        if (this.status == CLOSE) {
            return;
        }
        synchronized (this) {
            if (this.status == CLOSE) {
                return;
            }
            if (this.partitionWatcher != null) {
                this.partitionWatcher.unwatch();
                this.partitionWatcher = null;
            }
            doShutdown();
            nodePartitionTaskMap.forEach((id, taskMap) -> taskMap.forEach(PartitionTask::close));
            nodePartitionTaskMap.clear();
            if (this.lessee != null) {
                this.lessee.shutdown();
                this.lessee = null;
            }
            this.status = CLOSE;
        }
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
                execute(watcher);
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

    private void execute(NameNodesWatcher<NodePartition<N>> watcher) {
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
        return Math.abs(keyHash.hash(key, 0));
    }

    /**
     * 节点hash 值
     *
     * @param node  节点
     * @param index 第几分区
     * @return 返回 hash 值
     */
    private long nodeHash(N node, int index) {
        return Math.abs(nodeHash.hash(node, index));
    }

    private void registerPartition(PartitionTask task, CompletableFuture<ShardingPartition<N>> future) {
        NodePartition<N> partition = task.getPartition();
        if (lessee.isLive()) {
            explorer.add(path + NumberFormatAide.alignDigits(partition.getSlot(), keyHash.getMax()), partitionMineType, partition, lessee)
                    .whenComplete((nameNode, cause) -> {
                        if (nameNode != null) {
                            task.onSuccess(future);
                        } else {
                            if (cause != null) {
                                task.onException(future);
                                LOGGER.error("registerPartition {} failed", partition, cause);
                            } else {
                                task.onFailed(future);
                            }
                        }
                    });
        } else {
            if (lessee.isShutdown()) {
                future.cancel(true);
            } else {
                retry(() -> task.tryAgain(future), 5000);
            }
        }
    }

    private void restorePartition() {
        if (restoring.compareAndSet(false, true)) {
            synchronized (this) {
                allPartitionTaskStream().forEach(PartitionTask::register);
            }
            restoring.set(false);
        }
    }

    private Stream<EtcdNodeHashing<N>.PartitionTask> allPartitionTaskStream() {
        return nodePartitionTaskMap.values().stream().flatMap(Collection::stream);
    }

    protected class PartitionTask {

        private final NodePartition<N> partition;

        private int seed;

        private volatile boolean sync = false;

        private volatile boolean close = false;

        private final boolean rehash;

        protected PartitionTask(int index, N node, long slotIndex, boolean rehash) {
            this.partition = new NodePartition<>(index, node);
            partition.setSlot(slotIndex);
            this.rehash = rehash;
            this.seed = 0;
        }

        private NodePartition<N> getPartition() {
            return partition;
        }

        protected void close() {
            this.close = true;
        }

        protected CompletableFuture<ShardingPartition<N>> register() {
            CompletableFuture<ShardingPartition<N>> future = new CompletableFuture<>();
            synchronized (this) {
                if (close) {
                    future.cancel(true);
                } else {
                    if (!sync) {
                        registerPartition(this, future);
                        // 关闭 sync 状态
                        future.whenComplete((p, cause) -> finish());
                        sync = true;
                    } else {
                        future.completeExceptionally(new HashPartitionException("Partition is in the synchronous"));
                    }
                }
            }
            return future;
        }

        private void tryAgain(CompletableFuture<ShardingPartition<N>> future) {
            if (close) {
                future.cancel(true);
            } else {
                registerPartition(this, future);
            }
        }

        private void tryNext(CompletableFuture<ShardingPartition<N>> future) {
            if (close) {
                future.cancel(true);
            } else {
                if (rehash) {
                    this.seed++;
                    partition.setSlot(nodeHash(partition.getNode(), this.seed));
                    registerPartition(this, future);
                }
            }
        }

        private void onException(CompletableFuture<ShardingPartition<N>> future) {
            retry(() -> this.tryAgain(future), 3000);
        }

        private void onFailed(CompletableFuture<ShardingPartition<N>> future) {
            if (rehash) {
                retry(() -> this.tryNext(future), 3000);
            } else {
                future.complete(null);
            }
        }

        private void onSuccess(CompletableFuture<ShardingPartition<N>> future) {
            if (future != null) {
                future.complete(partition);
            }
        }

        private void finish() {
            synchronized (this) {
                sync = false;
            }
        }

    }

}


