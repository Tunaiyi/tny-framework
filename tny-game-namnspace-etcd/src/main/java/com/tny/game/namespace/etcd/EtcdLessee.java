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

import com.tny.game.common.event.firer.*;
import com.tny.game.namespace.*;
import com.tny.game.namespace.exception.*;
import com.tny.game.namespace.listener.*;
import io.etcd.jetcd.Lease;
import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.etcd.jetcd.support.CloseableClient;
import io.grpc.stub.StreamObserver;
import org.slf4j.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/6/29 03:22
 **/
public class EtcdLessee implements Lessee {

    private static final Logger LOGGER = LoggerFactory.getLogger(EtcdLessee.class);

    private static final int STOP = 0;

    private static final int GRANT = 1;

    private static final int LIVE = 2;

    private static final int PAUSE = 3;

    private static final int SHUTDOWN = 4;

    private final String name;

    private final Lease lease;

    private final AtomicInteger status = new AtomicInteger(STOP);

    private long ttl;

    private volatile EventFirer<LesseeListener, Lessee> firer;

    private CloseableClient keepalive;

    private volatile long leaseId = -1;

    EtcdLessee(String name, Lease lease, long ttl) {
        this.name = name;
        this.lease = lease;
        this.ttl = ttl;
    }

    @Override
    public long getId() {
        return leaseId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getTtl() {
        return ttl;
    }

    @Override
    public boolean isLive() {
        return this.status.get() == LIVE;
    }

    @Override
    public boolean isPause() {
        return this.status.get() == PAUSE;
    }

    @Override
    public boolean isStop() {
        return this.status.get() == STOP;
    }

    @Override
    public boolean isGranting() {
        return this.status.get() == GRANT;
    }

    @Override
    public boolean isShutdown() {
        return this.status.get() == SHUTDOWN;
    }

    @Override
    public EventSource<LesseeListener> event() {
        return firer();
    }

    @Override
    public CompletableFuture<Lessee> lease() {
        return lease(this.ttl);
    }

    @Override
    public CompletableFuture<Lessee> lease(long ttl) {
        return doGrant(ttl, STOP);
    }

    CompletableFuture<Lessee> resume() {
        return doGrant(ttl, PAUSE);
    }

    private CompletableFuture<Lessee> doGrant(long ttl, int whenStatus) {
        if (isLive()) {
            return CompletableFuture.completedFuture(this);
        }
        if (status.compareAndSet(whenStatus, GRANT)) {
            return lease.grant(TimeUnit.MILLISECONDS.toSeconds(ttl))
                    .whenComplete((response, cause) -> {
                        if (cause != null) {
                            LOGGER.error("Lease grant id exception", cause);
                            status.compareAndSet(GRANT, whenStatus);
                        } else {
                            this.ttl = ttl;
                            this.leaseId = response.getID();
                            this.keepalive = lease.keepAlive(this.leaseId, new StreamObserver<>() {

                                @Override
                                public void onNext(LeaseKeepAliveResponse value) {
                                    EventFirer<LesseeListener, Lessee> firer = EtcdLessee.this.firer;
                                    if (firer != null) {
                                        firer.fire(LesseeListener::onRenew, EtcdLessee.this);
                                    }
                                }

                                @Override
                                public void onError(Throwable cause) {
                                    if (status.compareAndSet(LIVE, PAUSE)) {
                                        doRevoke();
                                    }
                                    EventFirer<LesseeListener, Lessee> firer = EtcdLessee.this.firer;
                                    if (firer != null) {
                                        firer.fire(LesseeListener::onError, EtcdLessee.this, cause);
                                    }
                                }

                                @Override
                                public void onCompleted() {
                                    if (status.compareAndSet(LIVE, PAUSE)) {
                                        doRevoke();
                                    }
                                    EventFirer<LesseeListener, Lessee> firer = EtcdLessee.this.firer;
                                    if (firer != null) {
                                        firer.fire(LesseeListener::onCompleted, EtcdLessee.this);
                                    }
                                }
                            });
                            status.compareAndSet(GRANT, LIVE);
                            if (firer != null) {
                                if (whenStatus == PAUSE) {
                                    firer.fire(LesseeListener::onResume, EtcdLessee.this);
                                } else {
                                    firer.fire(LesseeListener::onLease, EtcdLessee.this);
                                }
                            }
                        }
                    }).thenApply(r -> this);
        }
        return failedFuture(new NamespaceLesseeException("Leaser status can not grant"));
    }

    @Override
    public CompletableFuture<Lessee> revoke() {
        if (isStop()) {
            return CompletableFuture.completedFuture(this);
        }
        if (status.compareAndSet(LIVE, STOP)) {
            return doRevoke();
        }
        return failedFuture(new NamespaceLesseeException("Leaser status can not revoke"));
    }

    @Override
    public CompletableFuture<Lessee> shutdown() {
        if (status.get() == SHUTDOWN) {
            return CompletableFuture.completedFuture(this);
        }
        status.set(SHUTDOWN);
        return doRevoke();
    }

    private EventFirer<LesseeListener, Lessee> firer() {
        if (firer != null) {
            return firer;
        }
        synchronized (this) {
            if (firer != null) {
                return firer;
            }
            this.firer = EventFirers.firer(LesseeListener.class);
            return this.firer;
        }
    }

    private <T> CompletableFuture<T> failedFuture(Throwable cause) {
        CompletableFuture<T> future = new CompletableFuture<>();
        future.completeExceptionally(cause);
        return future;
    }

    private CompletableFuture<Lessee> doRevoke() {
        CloseableClient client = this.keepalive;
        if (client != null) {
            client.close();
        }
        return lease.revoke(this.leaseId)
                .whenComplete((result, cause) -> {
                    if (cause != null) {
                        LOGGER.error("Lease revoke Id[{}] exception", this.leaseId, cause);
                    }
                })
                .thenApply(r -> this);
    }

}
