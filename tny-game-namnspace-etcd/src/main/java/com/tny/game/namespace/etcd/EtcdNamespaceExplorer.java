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
import com.tny.game.common.concurrent.*;
import com.tny.game.namespace.*;
import com.tny.game.namespace.exception.*;
import com.tny.game.namespace.listener.*;
import com.tny.game.namespace.sharding.*;
import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.*;
import io.etcd.jetcd.op.*;
import io.etcd.jetcd.options.*;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/6/29 16:11
 **/
public class EtcdNamespaceExplorer extends EtcdObject implements NamespaceExplorer {

    private static final ScheduledExecutorService SCHEDULED = Executors.newScheduledThreadPool(1, new CoreThreadFactory("EtcdNamespaceExplorer"));

    private final Client client;

    private final KV kv;

    private final Watch watch;

    private final Lease lease;

    private static final GetOption GET_MATCH_OPTION = GetOption.newBuilder().isPrefix(true).build();

    private static final DeleteOption DEL_OPTION = DeleteOption.newBuilder().withPrevKV(true).build();

    private static final DeleteOption DEL_MATCH_WITH_PREV_OPTION = DeleteOption.newBuilder().isPrefix(true).withPrevKV(true).build();

    private static final DeleteOption DEL_MATCH_OPTION = DeleteOption.newBuilder().isPrefix(true).withPrevKV(true).build();

    private final Map<String, EtcdLessee> leasers = new ConcurrentHashMap<>();

    private final LesseeListener lesseeListener = new LesseeListener() {

        @Override
        public void onRenew(Lessee source) {

        }

        @Override
        public void onError(Lessee source, Throwable cause) {
            handLessee(source.getName());
        }

        @Override
        public void onCompleted(Lessee source) {
            handLessee(source.getName());
        }

        @Override
        public void onResume(Lessee source) {

        }
    };

    public EtcdNamespaceExplorer(Client client, ObjectCodecAdapter objectCodecAdapter, Charset charset) {
        super(objectCodecAdapter, charset);
        this.client = client;
        this.kv = client.getKVClient();
        this.lease = client.getLeaseClient();
        this.watch = client.getWatchClient();
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> get(String path, ObjectMimeType<T> type) {
        return kv.get(toBytes(path)).thenApply(response -> {
            if (response.getCount() == 0) {
                return null;
            } else {
                return decodeKeyValue(response.getKvs(), type);
            }
        });
    }

    @Override
    public <T> CompletableFuture<List<NameNode<T>>> findAll(String path, ObjectMimeType<T> type) {
        return kv.get(toBytes(path), GET_MATCH_OPTION)
                .thenApply(response -> decodeAllKeyValues(response.getKvs(), type));
    }

    @Override
    public <T> CompletableFuture<List<NameNode<T>>> findAll(String from, String to, ObjectMimeType<T> type) {
        return kv.get(toBytes(from), GetOption.newBuilder().withRange(toBytes(to)).build())
                .thenApply(response -> decodeAllKeyValues(response.getKvs(), type));
    }

    @Override
    public <T extends ShardingNode> NodeHashing<T> nodeHashing(String rootPath, NodeHashingFactory factory, HashingOptions<T> options) {
        if (StringUtils.isBlank(rootPath)) {
            throw new NamespaceHashingException("rootPath {} is blank", rootPath);
        }
        if (factory == null) {
            factory = EtcdNodeHashingMultimapFactory.getDefault();
        }
        return factory.create(rootPath, options, this, this.objectCodecAdapter);
    }

    @Override
    public <T> HashingSubscriber<T> hashingSubscriber(String parentPath, long maxSlotSize, ObjectMimeType<T> mineType) {
        if (StringUtils.isBlank(parentPath)) {
            throw new NamespaceHashingException("rootPath {} is blank", parentPath);
        }
        return new EtcdHashingSubscriber<>(parentPath, maxSlotSize, mineType, this);
    }

    @Override
    public <K, T> HashingPublisher<K, T> hashingPublisher(String parentPath, long maxSlotSize, Hasher<T> hasher, ObjectMimeType<T> mineType) {
        if (StringUtils.isBlank(parentPath)) {
            throw new NamespaceHashingException("rootPath {} is blank", parentPath);
        }
        return new EtcdHashingPublisher<>(parentPath, maxSlotSize, hasher, mineType, this);
    }

    @Override
    public <T> NameNodesWatcher<T> nodeWatcher(String path, ObjectMimeType<T> type) {
        return new EtcdNameNodesWatcher<>(path, false, kv, watch, type, this.objectCodecAdapter, charset);
    }

    @Override
    public <T> NameNodesWatcher<T> allNodeWatcher(String path, ObjectMimeType<T> type) {
        return new EtcdNameNodesWatcher<>(path, true, kv, watch, type, this.objectCodecAdapter, charset);
    }

    @Override
    public <T> NameNodesWatcher<T> allNodeWatcher(String from, String to, ObjectMimeType<T> type) {
        return new EtcdNameNodesWatcher<>(from, to, true, kv, watch, type, this.objectCodecAdapter, charset);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> getOrAdd(String path, ObjectMimeType<T> type, T value) {
        return doAdd(path, type, value, true, null);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> getOrAdd(String path, ObjectMimeType<T> type, T value, Lessee lessee) {
        return doAdd(path, type, value, true, lessee);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> add(String path, ObjectMimeType<T> type, T value) {
        return doAdd(path, type, value, false, null);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> add(String path, ObjectMimeType<T> type, T value, Lessee lessee) {
        return doAdd(path, type, value, false, lessee);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> save(String path, ObjectMimeType<T> type, T value) {
        return doSave(path, type, value, null);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> save(String path, ObjectMimeType<T> type, T value, Lessee lessee) {
        return doSave(path, type, value, lessee);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> update(String path, ObjectMimeType<T> type, T value) {
        return doUpdate(path, type, value, null, null, null, 0L, RangeBorder.UNLIMITED, 0L, RangeBorder.UNLIMITED);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> update(String path, ObjectMimeType<T> type, T value, Lessee lessee) {
        return doUpdate(path, type, value, lessee, null, null, 0L, RangeBorder.UNLIMITED, 0L, RangeBorder.UNLIMITED);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> updateIf(String path, ObjectMimeType<T> type, T expect, T value) {
        return doUpdate(path, type, value, null, null, expect, 0L, RangeBorder.UNLIMITED, 0L, RangeBorder.UNLIMITED);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> updateIf(String path, ObjectMimeType<T> type, T expect, T value, Lessee lessee) {
        return doUpdate(path, type, value, lessee, null, expect, 0L, RangeBorder.UNLIMITED, 0L, RangeBorder.UNLIMITED);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> updateIf(String path, long version, ObjectMimeType<T> type, T value) {
        return doUpdate(path, type, value, null, null, null, version, RangeBorder.CLOSE, version, RangeBorder.CLOSE);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> updateIf(String path, long version, ObjectMimeType<T> type, T value, Lessee lessee) {
        return doUpdate(path, type, value, lessee, null, null, version, RangeBorder.CLOSE, version, RangeBorder.CLOSE);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> updateIf(String path, long minVersion, RangeBorder minBorder, long maxVersion, RangeBorder maxBorder,
            ObjectMimeType<T> type, T value) {
        return doUpdate(path, type, value, null, null, null, minVersion, minBorder, maxVersion, maxBorder);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> updateIf(String path, long minVersion, RangeBorder minBorder, long maxVersion, RangeBorder maxBorder,
            ObjectMimeType<T> type, T value, Lessee lessee) {
        return doUpdate(path, type, value, lessee, null, null, minVersion, minBorder, maxVersion, maxBorder);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> updateById(String path, long id, ObjectMimeType<T> type, T value) {
        return doUpdate(path, type, value, null, id, null, 0L, RangeBorder.UNLIMITED, 0L, RangeBorder.UNLIMITED);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> updateById(String path, long id, ObjectMimeType<T> type, T value, Lessee lessee) {
        return doUpdate(path, type, value, lessee, id, null, 0L, RangeBorder.UNLIMITED, 0L, RangeBorder.UNLIMITED);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> updateByIdAndIf(String path, long id, ObjectMimeType<T> type, T expect, T value) {
        return doUpdate(path, type, value, null, id, expect, 0L, RangeBorder.UNLIMITED, 0L, RangeBorder.UNLIMITED);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> updateByIdAndIf(String path, long id, ObjectMimeType<T> type, T expect, T value, Lessee lessee) {
        return doUpdate(path, type, value, lessee, id, expect, 0L, RangeBorder.UNLIMITED, 0L, RangeBorder.UNLIMITED);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> updateByIdAndIf(String path, long id, long version, ObjectMimeType<T> type, T value) {
        return doUpdate(path, type, value, null, id, null, version, RangeBorder.CLOSE, version, RangeBorder.CLOSE);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> updateByIdAndIf(String path, long id, long version, ObjectMimeType<T> type, T value, Lessee lessee) {
        return doUpdate(path, type, value, lessee, id, null, version, RangeBorder.CLOSE, version, RangeBorder.CLOSE);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> updateByIdAndIf(String path, long id, long minVersion, RangeBorder minBorder, long maxVersion,
            RangeBorder maxBorder, ObjectMimeType<T> type, T value) {
        return doUpdate(path, type, value, null, id, null, minVersion, minBorder, maxVersion, maxBorder);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> updateByIdAndIf(String path, long id, long minVersion, RangeBorder minBorder, long maxVersion,
            RangeBorder maxBorder, ObjectMimeType<T> type, T value, Lessee lessee) {
        return doUpdate(path, type, value, lessee, id, null, minVersion, minBorder, maxVersion, maxBorder);
    }

    @Override
    public CompletableFuture<Boolean> remove(String path) {
        return kv.delete(toBytes(path)).thenApply((response) -> response.getDeleted() > 0);
    }

    @Override
    public CompletableFuture<Long> removeAll(String path) {
        return kv.delete(toBytes(path), DEL_MATCH_OPTION).thenApply(DeleteResponse::getDeleted);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> removeAndGet(String path, ObjectMimeType<T> type) {
        return doDelete(path, type, null, null, 0L, RangeBorder.UNLIMITED, 0L, RangeBorder.UNLIMITED);
    }

    @Override
    public <T> CompletableFuture<List<NameNode<T>>> removeAllAndGet(String path, ObjectMimeType<T> type) {
        return kv.delete(toBytes(path), DEL_MATCH_WITH_PREV_OPTION).thenApply(response -> decodeAllKeyValues(response.getPrevKvs(), type));
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> removeIf(String path, ObjectMimeType<T> type, T expect) {
        return doDelete(path, type, null, expect, 0L, RangeBorder.UNLIMITED, 0L, RangeBorder.UNLIMITED);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> removeIf(String path, long version, ObjectMimeType<T> type) {
        return doDelete(path, type, null, null, version, RangeBorder.CLOSE, version, RangeBorder.CLOSE);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> removeIf(String path, long minVersion, RangeBorder minBorder, long maxVersion, RangeBorder maxBorder,
            ObjectMimeType<T> type) {
        return doDelete(path, type, null, null, minVersion, minBorder, maxVersion, maxBorder);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> removeById(String path, long id, ObjectMimeType<T> type) {
        return doDelete(path, type, id, null, 0L, RangeBorder.UNLIMITED, 0L, RangeBorder.UNLIMITED);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> removeByIdAndIf(String path, long id, ObjectMimeType<T> type, T expect) {
        return doDelete(path, type, id, expect, 0L, RangeBorder.UNLIMITED, 0L, RangeBorder.UNLIMITED);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> removeByIdAndIf(String path, long id, long version, ObjectMimeType<T> type) {
        return doDelete(path, type, id, null, version, RangeBorder.CLOSE, version, RangeBorder.CLOSE);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> removeByIdAndIf(String path, long id, long minVersion, RangeBorder minBorder, long maxVersion,
            RangeBorder maxBorder, ObjectMimeType<T> type) {
        return doDelete(path, type, id, null, minVersion, minBorder, maxVersion, maxBorder);
    }

    public CompletableFuture<TxnResponse> transact(Consumer<Txn> consumer) {
        return inTxn(consumer);
    }

    @Override
    public CompletableFuture<Lessee> lease(String name, long ttl) {
        EtcdLessee lessee = leasers.get(name);
        if (lessee != null) {
            return CompletableFuture.completedFuture(lessee);
        }
        lessee = new EtcdLessee(name, lease, ttl);
        EtcdLessee old = leasers.putIfAbsent(lessee.getName(), lessee);
        if (old != null) {
            return CompletableFuture.completedFuture(old);
        }
        lessee.event().add(lesseeListener);
        return lessee.lease();
    }

    private CompletableFuture<TxnResponse> inTxn(Consumer<Txn> consumer) {
        Txn txn = kv.txn();
        consumer.accept(txn);
        return txn.commit();
    }

    private <T> CompletableFuture<NameNode<T>> doAdd(String path, ObjectMimeType<T> type, T value, boolean loadIfExist, Lessee lessee) {
        return inTxn(txn -> {
            ByteSequence key = toBytes(path);
            ByteSequence valueBytes = encode(value, type);
            Op getOp = Op.get(key, GetOption.DEFAULT);
            PutOption option = lessee != null ? PutOption.newBuilder().withLeaseId(lessee.getId()).build() : PutOption.DEFAULT;
            txn.If(new Cmp(key, Cmp.Op.EQUAL, CmpTarget.version(0))).Then(Op.put(key, valueBytes, option), getOp);
            if (loadIfExist) {
                txn.Else(getOp);
            }
        }).thenApply(txn -> decodeOne(txn.getGetResponses(), type));
    }

    private <T> CompletableFuture<NameNode<T>> doSave(String path, ObjectMimeType<T> type, T value, Lessee lessee) {
        return inTxn(txn -> {
            ByteSequence key = toBytes(path);
            ByteSequence valueBytes = encode(value, type);
            PutOption option = PutOption.DEFAULT;
            if (lessee != null) {
                option = PutOption.newBuilder().withLeaseId(lessee.getId()).build();
            }
            txn.Then(Op.put(key, valueBytes, option), Op.get(key, GetOption.DEFAULT));
        }).thenApply(txn -> decodeOne(txn.getGetResponses(), type));
    }

    private <T> CompletableFuture<NameNode<T>> doUpdate(String path, ObjectMimeType<T> type, T value, Lessee lessee, Long id, T expect,
            long minVersion,
            RangeBorder minBorder,
            long maxVersion, RangeBorder maxBorder) {
        return inTxn(txn -> {
            ByteSequence key = toBytes(path);
            ByteSequence valueBytes = encode(value, type);
            ByteSequence expectBytes = expect == null ? null : encode(expect, type);
            Op getOp = Op.get(key, GetOption.DEFAULT);
            PutOption option = lessee != null ? PutOption.newBuilder().withLeaseId(lessee.getId()).build() : PutOption.DEFAULT;
            List<Cmp> cmpList = createCmpList(id, expectBytes, key, minVersion, minBorder, maxVersion, maxBorder);
            txn.If(cmpList.toArray(new Cmp[0])).Then(Op.put(key, valueBytes, option), getOp);
        }).thenApply(txn -> decodeOne(txn.getGetResponses(), type));
    }

    private <T> CompletableFuture<NameNode<T>> doDelete(String path, ObjectMimeType<T> type, Long id, T expect, long minVersion,
            RangeBorder minBorder, long maxVersion, RangeBorder maxBorder) {
        return inTxn(txn -> {
            ByteSequence key = toBytes(path);
            ByteSequence expectBytes = expect == null ? null : encode(expect, type);
            List<Cmp> cmpList = createCmpList(id, expectBytes, key, minVersion, minBorder, maxVersion, maxBorder);
            txn.If(cmpList.toArray(new Cmp[0])).Then(Op.delete(key, DEL_OPTION));
        }).thenApply(txn -> {
            List<DeleteResponse> responses = txn.getDeleteResponses();
            if (responses.isEmpty()) {
                return null;
            }
            DeleteResponse response = responses.get(0);
            return decodeKeyValue(response.getPrevKvs(), type);
        });
    }

    private <T> List<Cmp> createCmpList(Long id, ByteSequence expect, ByteSequence key, long minVersion, RangeBorder minBorder, long maxVersion,
            RangeBorder maxBorder) {
        List<Cmp> cmpList = versionCmpList(key, minVersion, minBorder, maxVersion, maxBorder);
        if (id != null) {
            cmpList.add(new Cmp(key, Cmp.Op.EQUAL, CmpTarget.createRevision(id)));
        }
        if (expect != null) {
            cmpList.add(new Cmp(key, Cmp.Op.EQUAL, CmpTarget.value(expect)));
        }
        if (cmpList.isEmpty()) {
            cmpList.add(new Cmp(key, Cmp.Op.GREATER, CmpTarget.version(0)));
        }
        return cmpList;
    }

    private List<Cmp> versionCmpList(ByteSequence key, long minVersion, RangeBorder minBorder, long maxVersion, RangeBorder maxBorder) {
        if (minBorder == RangeBorder.UNLIMITED && maxBorder == RangeBorder.UNLIMITED) {
            return new ArrayList<>();
        }
        if (minBorder != RangeBorder.UNLIMITED && maxBorder != RangeBorder.UNLIMITED && maxVersion < minVersion) {
            throw new NamespaceNodeException(format("minVersion : {}, maxVersion : {}, maxVersion must > or = minVersion", minVersion, maxVersion));
        }
        List<Cmp> cmpList = new ArrayList<>();
        if (minBorder == RangeBorder.CLOSE && maxBorder == RangeBorder.CLOSE && Objects.equals(maxVersion, minVersion)) {
            cmpList.add(new Cmp(key, Cmp.Op.EQUAL, CmpTarget.version(maxVersion)));
        } else {
            switch (minBorder) {
                case OPEN:
                    cmpList.add(new Cmp(key, Cmp.Op.GREATER, CmpTarget.version(minVersion)));
                    break;
                case CLOSE:
                    cmpList.add(new Cmp(key, Cmp.Op.GREATER, CmpTarget.version(minVersion - 1)));
                    break;
                case UNLIMITED:
                    break;
            }
            switch (maxBorder) {
                case OPEN:
                    cmpList.add(new Cmp(key, Cmp.Op.LESS, CmpTarget.version(maxVersion)));
                    break;
                case CLOSE:
                    cmpList.add(new Cmp(key, Cmp.Op.LESS, CmpTarget.version(maxVersion + 1)));
                    break;
                case UNLIMITED:
                    break;
            }
        }
        return cmpList;
    }

    private void handLessee(String name) {
        EtcdLessee lessee = leasers.get(name);
        if (lessee == null) {
            return;
        }
        if (lessee.isPause()) {
            resume(lessee, 3000);
            return;
        }
        if (lessee.isShutdown()) {
            this.leasers.remove(name, lessee);
        }
    }

    private void resume(EtcdLessee lessee, long delay) {
        if (lessee.isPause()) {
            SCHEDULED.schedule(() -> {
                lessee.resume().whenComplete((l, cause) -> {
                    if (cause != null) {
                        resume(lessee, delay);
                    }
                });
            }, delay, TimeUnit.MILLISECONDS);
        }
    }

    private <T> NameNode<T> decodeOne(List<GetResponse> responses, ObjectMimeType<T> type) {
        if (responses.isEmpty()) {
            return null;
        }
        return decodeKeyValue(responses.get(0).getKvs(), type);
    }

    public void shutdown() {
        leasers.forEach((k, l) -> l.shutdown());
        watch.close();
        lease.close();
        kv.close();
        client.close();
    }

}
