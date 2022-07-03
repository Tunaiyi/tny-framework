package com.tny.game.namespace.etcd;

import com.tny.game.codec.*;
import com.tny.game.namespace.*;
import com.tny.game.namespace.etcd.exception.*;
import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.*;
import io.etcd.jetcd.op.*;
import io.etcd.jetcd.options.*;

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
public class EtcdNamespaceExplorer extends EtcdAccess implements NamespaceExplorer {

    private final Client client;

    private final KV kv;

    private final Watch watch;

    private final Lease lease;

    private static final GetOption GET_MATCH_OPTION = GetOption.newBuilder().isPrefix(true).build();

    private static final DeleteOption DEL_OPTION = DeleteOption.newBuilder().withPrevKV(true).build();

    private static final DeleteOption DEL_MATCH_OPTION = DeleteOption.newBuilder().isPrefix(true).withPrevKV(true).build();

    private final Map<String, Lessee> leasers = new ConcurrentHashMap<>();

    public EtcdNamespaceExplorer(Client client, ObjectCodecAdapter objectCodecAdapter, Charset charset) {
        super(objectCodecAdapter, charset);
        this.client = client;
        this.kv = client.getKVClient();
        this.lease = client.getLeaseClient();
        this.watch = client.getWatchClient();
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> get(String path, ObjectMineType<T> type) {
        return kv.get(toBytes(path)).thenApply(response -> {
            if (response.getCount() == 0) {
                return null;
            } else {
                return decodeKeyValue(response.getKvs(), type);
            }
        });
    }

    @Override
    public <T> CompletableFuture<List<NameNode<T>>> findAll(String path, ObjectMineType<T> type) {
        return kv.get(toBytes(path), GET_MATCH_OPTION).thenApply(response -> decodeAllKeyValues(response.getKvs(), type));
    }

    @Override
    public <T> NameNodesWatcher<T> nodeWatcher(String path, ObjectMineType<T> type) {
        return new EtcdNameNodesWatcher<>(path, false, kv, watch, type, this.objectCodecAdapter, charset);
    }

    @Override
    public <T> NameNodesWatcher<T> allNodeWatcher(String path, ObjectMineType<T> type) {
        return new EtcdNameNodesWatcher<>(path, true, kv, watch, type, this.objectCodecAdapter, charset);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> getOrAdd(String path, ObjectMineType<T> type, T value) {
        return doAdd(path, type, value, true, null);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> getOrAdd(String path, ObjectMineType<T> type, T value, Lessee lessee) {
        return doAdd(path, type, value, true, lessee);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> add(String path, ObjectMineType<T> type, T value) {
        return doAdd(path, type, value, false, null);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> add(String path, ObjectMineType<T> type, T value, Lessee lessee) {
        return doAdd(path, type, value, false, lessee);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> save(String path, ObjectMineType<T> type, T value) {
        return doSave(path, type, value, null);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> save(String path, ObjectMineType<T> type, T value, Lessee lessee) {
        return doSave(path, type, value, lessee);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> update(String path, ObjectMineType<T> type, T value) {
        return doUpdate(path, type, value, null, null, null, 0, RangeBorder.UNLIMITED, 0, RangeBorder.UNLIMITED);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> update(String path, ObjectMineType<T> type, T value, Lessee lessee) {
        return doUpdate(path, type, value, lessee, null, null, 0, RangeBorder.UNLIMITED, 0, RangeBorder.UNLIMITED);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> updateIf(String path, ObjectMineType<T> type, T expect, T value) {
        return doUpdate(path, type, value, null, null, expect, 0, RangeBorder.UNLIMITED, 0, RangeBorder.UNLIMITED);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> updateIf(String path, ObjectMineType<T> type, T expect, T value, Lessee lessee) {
        return doUpdate(path, type, value, lessee, null, expect, 0, RangeBorder.UNLIMITED, 0, RangeBorder.UNLIMITED);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> updateIf(String path, long version, ObjectMineType<T> type, T value) {
        return doUpdate(path, type, value, null, null, null, version, RangeBorder.CLOSE, version, RangeBorder.CLOSE);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> updateIf(String path, long version, ObjectMineType<T> type, T value, Lessee lessee) {
        return doUpdate(path, type, value, lessee, null, null, version, RangeBorder.CLOSE, version, RangeBorder.CLOSE);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> updateIf(String path, long minVersion, RangeBorder minBorder, long maxVersion, RangeBorder maxBorder,
            ObjectMineType<T> type, T value) {
        return doUpdate(path, type, value, null, null, null, minVersion, minBorder, maxVersion, maxBorder);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> updateIf(String path, long minVersion, RangeBorder minBorder, long maxVersion, RangeBorder maxBorder,
            ObjectMineType<T> type, T value, Lessee lessee) {
        return doUpdate(path, type, value, lessee, null, null, minVersion, minBorder, maxVersion, maxBorder);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> updateById(String path, long id, ObjectMineType<T> type, T value) {
        return doUpdate(path, type, value, null, id, null, 0, RangeBorder.UNLIMITED, 0, RangeBorder.UNLIMITED);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> updateById(String path, long id, ObjectMineType<T> type, T value, Lessee lessee) {
        return doUpdate(path, type, value, lessee, id, null, 0, RangeBorder.UNLIMITED, 0, RangeBorder.UNLIMITED);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> updateByIdAndIf(String path, long id, ObjectMineType<T> type, T expect, T value) {
        return doUpdate(path, type, value, null, id, expect, 0, RangeBorder.UNLIMITED, 0, RangeBorder.UNLIMITED);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> updateByIdAndIf(String path, long id, ObjectMineType<T> type, T expect, T value, Lessee lessee) {
        return doUpdate(path, type, value, lessee, id, expect, 0, RangeBorder.UNLIMITED, 0, RangeBorder.UNLIMITED);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> updateByIdAndIf(String path, long id, long version, ObjectMineType<T> type, T value) {
        return doUpdate(path, type, value, null, id, null, version, RangeBorder.CLOSE, version, RangeBorder.CLOSE);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> updateByIdAndIf(String path, long id, long version, ObjectMineType<T> type, T value, Lessee lessee) {
        return doUpdate(path, type, value, lessee, id, null, version, RangeBorder.CLOSE, version, RangeBorder.CLOSE);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> updateByIdAndIf(String path, long id, long minVersion, RangeBorder minBorder, long maxVersion,
            RangeBorder maxBorder, ObjectMineType<T> type, T value) {
        return doUpdate(path, type, value, null, id, null, minVersion, minBorder, maxVersion, maxBorder);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> updateByIdAndIf(String path, long id, long minVersion, RangeBorder minBorder, long maxVersion,
            RangeBorder maxBorder, ObjectMineType<T> type, T value, Lessee lessee) {
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
    public <T> CompletableFuture<NameNode<T>> removeAndGet(String path, ObjectMineType<T> type) {
        return doDelete(path, type, null, null, 0, RangeBorder.UNLIMITED, 0, RangeBorder.UNLIMITED);
    }

    @Override
    public <T> CompletableFuture<List<NameNode<T>>> removeAllAndGet(String path, ObjectMineType<T> type) {
        return kv.delete(toBytes(path), DEL_MATCH_OPTION).thenApply(response -> decodeAllKeyValues(response.getPrevKvs(), type));
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> removeIf(String path, ObjectMineType<T> type, T expect) {
        return doDelete(path, type, null, expect, 0, RangeBorder.UNLIMITED, 0, RangeBorder.UNLIMITED);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> removeIf(String path, long version, ObjectMineType<T> type) {
        return doDelete(path, type, null, null, version, RangeBorder.CLOSE, version, RangeBorder.CLOSE);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> removeIf(String path, long minVersion, RangeBorder minBorder, long maxVersion, RangeBorder maxBorder,
            ObjectMineType<T> type) {
        return doDelete(path, type, null, null, minVersion, minBorder, maxVersion, maxBorder);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> removeById(String path, long id, ObjectMineType<T> type) {
        return doDelete(path, type, id, null, 0, RangeBorder.UNLIMITED, 0, RangeBorder.UNLIMITED);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> removeByIdAndIf(String path, long id, ObjectMineType<T> type, T expect) {
        return doDelete(path, type, id, expect, 0, RangeBorder.UNLIMITED, 0, RangeBorder.UNLIMITED);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> removeByIdAndIf(String path, long id, long version, ObjectMineType<T> type) {
        return doDelete(path, type, id, null, version, RangeBorder.CLOSE, version, RangeBorder.CLOSE);
    }

    @Override
    public <T> CompletableFuture<NameNode<T>> removeByIdAndIf(String path, long id, long minVersion, RangeBorder minBorder, long maxVersion,
            RangeBorder maxBorder, ObjectMineType<T> type) {
        return doDelete(path, type, id, null, minVersion, minBorder, maxVersion, maxBorder);
    }

    @Override
    public CompletableFuture<Lessee> lease(String name, long ttl) {
        Lessee lessee = leasers.get(name);
        if (lessee != null) {
            return CompletableFuture.completedFuture(lessee);
        }
        lessee = new EtcdLessee(name, lease, ttl);
        Lessee old = leasers.putIfAbsent(lessee.getName(), lessee);
        if (old != null) {
            return CompletableFuture.completedFuture(old);
        }
        return lessee.grant();
    }

    private CompletableFuture<TxnResponse> inTxn(Consumer<Txn> consumer) {
        Txn txn = kv.txn();
        consumer.accept(txn);
        return txn.commit();
    }

    private <T> CompletableFuture<NameNode<T>> doAdd(String path, ObjectMineType<T> type, T value, boolean loadIfExist, Lessee lessee) {
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

    private <T> CompletableFuture<NameNode<T>> doSave(String path, ObjectMineType<T> type, T value, Lessee lessee) {
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

    private <T> CompletableFuture<NameNode<T>> doUpdate(String path, ObjectMineType<T> type, T value, Lessee lessee, Long id, T expect,
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

    private <T> CompletableFuture<NameNode<T>> doDelete(String path, ObjectMineType<T> type, Long id, T expect, long minVersion,
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
        List<Cmp> cmpList = toVersionCmpList(key, minVersion, minBorder, maxVersion, maxBorder);
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

    private List<Cmp> toVersionCmpList(ByteSequence key, long minVersion, RangeBorder minBorder, long maxVersion, RangeBorder maxBorder) {
        if (minBorder == RangeBorder.UNLIMITED && maxBorder == RangeBorder.UNLIMITED) {
            return new ArrayList<>();
        }
        if (minBorder != RangeBorder.UNLIMITED && maxBorder != RangeBorder.UNLIMITED && maxVersion < minVersion) {
            throw new NameNodeException(format("minVersion : {}, maxVersion : {}, maxVersion must > or = minVersion", minVersion, maxVersion));
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

    private <T> NameNode<T> decodeOne(List<GetResponse> responses, ObjectMineType<T> type) {
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
