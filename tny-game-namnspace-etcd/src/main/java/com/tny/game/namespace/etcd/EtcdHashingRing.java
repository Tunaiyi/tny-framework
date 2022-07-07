package com.tny.game.namespace.etcd;

import com.tny.game.codec.*;
import com.tny.game.codec.jackson.*;
import com.tny.game.common.concurrent.*;
import com.tny.game.common.event.firer.*;
import com.tny.game.namespace.*;
import com.tny.game.namespace.consistenthash.*;
import com.tny.game.namespace.consistenthash.listener.*;
import com.tny.game.namespace.exception.*;
import com.tny.game.namespace.listener.*;
import org.slf4j.Logger;

import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.stream.Collectors;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;
import static org.slf4j.LoggerFactory.*;

/**
 * 一致性Hash 集群
 * <p>
 *
 * @author kgtny
 * @date 2022/7/7 04:04
 **/
public class EtcdHashingRing<N extends ShardingNode> extends EtcdObject implements HashingRing<N> {

    private static final AtomicInteger INDEX = new AtomicInteger();

    private static final ScheduledExecutorService SCHEDULED = Executors.newScheduledThreadPool(1, new CoreThreadFactory("EtcdHashRingScheduled"));

    private static final Logger LOGGER = getLogger(EtcdHashingRing.class);

    private static final int INIT = 0;

    private static final int STARTING = 1;

    private static final int EXECUTE = 2;

    private static final int CLOSE = 3;

    private final NamespaceExplorer explorer;

    // 名字
    private final String name;

    // 分区路径
    private final String rootPath;

    private final long ttl;

    // hash 算法
    private final HashAlgorithm algorithm;

    // 每一节点分区数
    private final int partitionCount;

    // Hash 环
    private final ShardingSet<N> ring;

    // 序列化类型
    private final ObjectMineType<ShardingPartition<N>> partitionMineType;

    // 本地节点
    private final Map<N, Map<Integer, PartitionTask>> nodePartitionTaskMap = new ConcurrentHashMap<>();

    // 分区恢复状态
    private final AtomicBoolean restoring = new AtomicBoolean();

    private final WatchListener<ShardingPartition<N>> partitionListener = new WatchListener<ShardingPartition<N>>() {

        @Override
        public void onLoad(NameNodesWatcher<ShardingPartition<N>> watcher, List<NameNode<ShardingPartition<N>>> nameNodes) {
            load(nameNodes.stream().map(NameNode::getValue).collect(Collectors.toList()));
        }

        @Override
        public void onCreate(NameNodesWatcher<ShardingPartition<N>> watcher, NameNode<ShardingPartition<N>> node) {
            save(node.getValue());
        }

        @Override
        public void onUpdate(NameNodesWatcher<ShardingPartition<N>> watcher, NameNode<ShardingPartition<N>> node) {
            save(node.getValue());
        }

        @Override
        public void onDelete(NameNodesWatcher<ShardingPartition<N>> watcher, NameNode<ShardingPartition<N>> node) {
            remove(node.getValue());
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

    private Lessee lessee;

    private NameNodesWatcher<ShardingPartition<N>> partitionWatcher;

    private volatile int status = INIT;

    protected EtcdHashingRing(String rootPath, HashingOptions<N> option, NamespaceExplorer explorer,
            ObjectCodecAdapter objectCodecAdapter, Charset charset) {
        super(objectCodecAdapter, charset);
        this.name = ifNotBlank(option.getName(), () -> "HashingRing-" + INDEX.incrementAndGet());
        this.explorer = explorer;
        this.ttl = option.getTtl();
        this.rootPath = NamespacePaths.dirPath(rootPath);
        this.algorithm = ifNull(option.getAlgorithm(), HashAlgorithms.XX3_32_HASH);
        this.partitionCount = Math.max(1, option.getPartitionCount());
        this.partitionMineType = ObjectMineType.of(option.getType(), JsonMimeType.JSON);
        this.ring = new ConsistentHashRing<>(name, algorithm);
    }

    @Override
    public CompletableFuture<HashingRing<N>> start() {
        synchronized (this) {
            CompletableFuture<HashingRing<N>> future = new CompletableFuture<>();
            if (status == INIT) {
                status = STARTING;
                if (this.lessee == null) {
                    explorer.lease(name, this.ttl).whenComplete((lessee, cause) -> {
                        if (cause != null) {
                            LOGGER.error("{} lease exception ", this.name, cause);
                            future.completeExceptionally(cause);
                        } else {
                            this.setLessee(lessee);
                        }
                    });
                }
                this.doWatch(future);
                return future;
            }
            future.completeExceptionally(new HashRingException(format("{} start failed, status is {}", this.name, this.status)));
            return future;
        }
    }

    /**
     * 注册节点
     *
     * @param node 节点
     * @return 返回注册的分区
     */
    @Override
    public CompletableFuture<List<RingPartition<N>>> register(N node) {
        if (this.status != EXECUTE) {
            return CompleteFutureAide.failedFuture(new HashRingException("ring is not start, status is {}", this.status));
        }
        synchronized (this) {
            if (this.status != EXECUTE) {
                return CompleteFutureAide.failedFuture(new HashRingException("ring is not start, status is {}", this.status));
            }
            Map<Integer, PartitionTask> partitions = new TreeMap<>();
            if (nodePartitionTaskMap.putIfAbsent(node, partitions) != null) {
                return CompleteFutureAide.failedFuture(new HashPartitionRegisteredException("register failed"));
            }
            return registerPartitions(node, partitions);
        }
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
                this.partitionWatcher.stop();
                this.partitionWatcher = null;
            }
            nodePartitionTaskMap.forEach((id, taskMap) -> taskMap.forEach((index, task) -> task.close()));
            nodePartitionTaskMap.clear();
            if (this.lessee != null) {
                this.lessee.shutdown();
                this.lessee = null;
            }
            this.status = CLOSE;
        }
    }

    @Override
    public boolean contains(Partition<N> partition) {
        return ring.contains(partition);
    }

    @Override
    public List<Partition<N>> findPartitions(String nodeId) {
        return ring.findPartitions(nodeId);
    }

    @Override
    public List<ShardingRange<N>> findRanges(String nodeId) {
        return ring.findRanges(nodeId);
    }

    @Override
    public List<Partition<N>> getAllPartitions() {
        return ring.getAllPartitions();
    }

    @Override
    public List<ShardingRange<N>> getAllRanges() {
        return ring.getAllRanges();
    }

    @Override
    public Optional<Partition<N>> prevPartition(long slot) {
        return ring.prevPartition(slot);
    }

    @Override
    public Optional<Partition<N>> nextPartition(long slot) {
        return ring.nextPartition(slot);
    }

    @Override
    public Optional<Partition<N>> locate(String key) {
        return ring.locate(key);
    }

    @Override
    public List<Partition<N>> locate(String key, int count) {
        return ring.locate(key, count);
    }

    @Override
    public int partitionSize() {
        return ring.partitionSize();
    }

    @Override
    public EventSource<ShardingListener<N>> changeEvent() {
        return ring.changeEvent();
    }

    private void doWatch(CompletableFuture<HashingRing<N>> future) {
        if (partitionWatcher != null) {
            future.complete(this);
            return;
        }
        partitionWatcher = explorer.allNodeWatcher(rootPath, partitionMineType);
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

    private void execute(NameNodesWatcher<ShardingPartition<N>> watcher) {
        synchronized (this) {
            if (status == STARTING) {
                this.partitionWatcher = watcher;
                this.status = EXECUTE;
            } else {
                watcher.stop();
            }
        }
    }

    private void load(List<Partition<N>> partitions) {
        synchronized (this) {
            this.ring.clear();
            this.ring.addAll(partitions);
        }
    }

    private void save(Partition<N> partition) {
        synchronized (this) {
            this.ring.save(partition);
        }
    }

    private void remove(Partition<N> partition) {
        synchronized (this) {
            this.ring.remove(partition.getSlot());
        }
    }

    private CompletableFuture<List<RingPartition<N>>> registerPartitions(N node, Map<Integer, PartitionTask> taskMap) {
        CompletableFuture<List<RingPartition<N>>> finalFuture = new CompletableFuture<>();
        List<CompletableFuture<RingPartition<N>>> futures = Collections.synchronizedList(new ArrayList<>());
        List<RingPartition<N>> successList = Collections.synchronizedList(new ArrayList<>());
        for (int index = 0; taskMap.size() < this.partitionCount; index++) {
            if (taskMap.containsKey(index)) {
                continue;
            }
            PartitionTask task = new PartitionTask(index, node);
            taskMap.put(task.getIndex(), task);
            CompletableFuture<RingPartition<N>> future = task.register();
            future.whenComplete((partition, cause) -> {
                if (partition != null) {
                    successList.add(partition);
                }
                futures.remove(future);
                if (futures.isEmpty()) {
                    finalFuture.complete(successList);
                }
            });
        }
        return finalFuture;
    }

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

    private long hash(String key, int seed) {
        return Math.abs(algorithm.hash(key, seed));
    }

    private void registerPartition(PartitionTask task, CompletableFuture<RingPartition<N>> future) {
        ShardingPartition<N> partition = task.getPartition();
        partition.setSlot(hash(partition.getKey(), task.getSeed()));
        if (lessee.isLive()) {
            explorer.add(rootPath + partition.getSlot(), partitionMineType, partition, lessee)
                    .whenComplete((nameNode, cause) -> {
                        if (nameNode != null) {
                            if (future != null) {
                                future.complete(partition);
                            }
                        } else {
                            if (cause != null) {
                                LOGGER.error("registerPartition {} failed", partition, cause);
                                retry(() -> task.tryAgain(future), 3000);
                            } else {
                                retry(() -> task.tryNext(future), 3000);
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
                nodePartitionTaskMap.forEach((id, taskMap) -> taskMap.forEach((index, task) -> task.register()));
            }
            restoring.set(false);
        }
    }

    private class PartitionTask {

        private final ShardingPartition<N> partition;

        private int seed = 0;

        private boolean sync = false;

        private volatile boolean close = false;

        private PartitionTask(int index, N node) {
            this.partition = new ShardingPartition<>(index, node);
        }

        private ShardingPartition<N> getPartition() {
            return partition;
        }

        private int getIndex() {
            return partition.getIndex();
        }

        private int getSeed() {
            return seed;
        }

        private void close() {
            this.close = true;
        }

        private void tryAgain(CompletableFuture<RingPartition<N>> future) {
            if (close) {
                future.cancel(true);
            } else {
                registerPartition(this, future);
            }
        }

        private void tryNext(CompletableFuture<RingPartition<N>> future) {
            if (close) {
                future.cancel(true);
            } else {
                this.seed++;
                registerPartition(this, future);
            }
        }

        private CompletableFuture<RingPartition<N>> register() {
            CompletableFuture<RingPartition<N>> future = new CompletableFuture<>();
            synchronized (this) {
                if (close) {
                    future.cancel(true);
                } else {
                    if (!sync) {
                        registerPartition(this, future);
                        future.whenComplete((p, cause) -> finish());
                        sync = true;
                    } else {
                        future.completeExceptionally(new HashPartitionException("Partition is in the synchronous"));
                    }
                }
            }
            return future;
        }

        private void finish() {
            synchronized (this) {
                sync = false;
            }
        }

    }

}


