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

package com.tny.game.common.worker;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * AbstractFuture/FutureTask 两枚私有 Sync 的逐字相同状态机段收敛（reduce-code-duplication D3：包内共享实现）。
 * <p>
 * 方法体逐字搬运自原两处克隆段（Doug Lea JDK1.5 变体同源拷贝），零行为变更：
 * <ul>
 * <li>共享段：RAN/CANCELLED 状态位、result/exception/runner 字段、tryAcquireShared/tryReleaseShared、
 * innerIsCancelled/innerIsDone、innerGet 两形态、innerSet/innerSetException/innerCancel、reset。</li>
 * <li>两侧差异：FutureTask 独有 RUNNING 态、callable 与 innerRun/innerRunAndReset 留其 Sync 本类；
 * done() 为抽象钩子，由各 Sync 子类转发到门面（外层）类的 protected done()，动态分派语义与原内部类
 * 直调外层方法逐字一致（子类覆写仍被触发、计次不变，已由 FutureStateMachineParityTest 钉桩）。</li>
 * </ul>
 * 本类为包私有实现细节，非发布 API；两类 public 骨架与 implements 关系冻结不动。
 * <p>
 * Uses AQS sync state to represent run status
 *
 * @param <V> The result type
 * @author Doug Lea
 */
abstract class FutureSyncSupport<V> extends AbstractQueuedSynchronizer {

    private static final long serialVersionUID = -7828117401763700385L;

    /**
     * State value representing that task ran
     */
    protected static final int RAN = 2;

    /**
     * State value representing that task was cancelled
     */
    protected static final int CANCELLED = 4;

    /**
     * The result to return from get()
     */
    private V result;

    /**
     * The exception to throw from get()
     */
    private Throwable exception;

    /**
     * The thread running task. When nulled after set/cancel, this indicates
     * that the results are accessible. Must be volatile, to ensure
     * visibility upon completion.
     */
    protected volatile Thread runner;

    /**
     * 钩子：由各 Sync 子类转发到门面类的 protected done()。
     */
    protected abstract void done();

    private boolean ranOrCancelled(int state) {
        return (state & (RAN | CANCELLED)) != 0;
    }

    /**
     * Implements AQS base acquire to succeed if ran or cancelled
     */
    @Override
    protected int tryAcquireShared(int ignore) {
        return innerIsDone() ? 1 : -1;
    }

    /**
     * Implements AQS base release to always signal after setting final done
     * status by nulling runner thread.
     */
    @Override
    protected boolean tryReleaseShared(int ignore) {
        this.runner = null;
        return true;
    }

    boolean innerIsCancelled() {
        return getState() == CANCELLED;
    }

    boolean innerIsDone() {
        return ranOrCancelled(getState()) && this.runner == null;
    }

    V innerGet() throws InterruptedException, ExecutionException {
        acquireSharedInterruptibly(0);
        if (getState() == CANCELLED) {
            throw new CancellationException();
        }
        if (this.exception != null) {
            throw new ExecutionException(this.exception);
        }
        return this.result;
    }

    V innerGet(long nanosTimeout) throws InterruptedException, ExecutionException, TimeoutException {
        if (!tryAcquireSharedNanos(0, nanosTimeout)) {
            throw new TimeoutException();
        }
        if (getState() == CANCELLED) {
            throw new CancellationException();
        }
        if (this.exception != null) {
            throw new ExecutionException(this.exception);
        }
        return this.result;
    }

    void innerSet(V v) {
        for (; ; ) {
            int s = getState();
            if (s == RAN) {
                return;
            }
            if (s == CANCELLED) {
                // aggressively release to set runner to null,
                // in case we are racing with a cancel request
                // that will try to interrupt runner
                releaseShared(0);
                return;
            }
            if (compareAndSetState(s, RAN)) {
                this.result = v;
                releaseShared(0);
                done();
                return;
            }
        }
    }

    void innerSetException(Throwable t) {
        for (; ; ) {
            int s = getState();
            if (s == RAN) {
                return;
            }
            if (s == CANCELLED) {
                // aggressively release to set runner to null,
                // in case we are racing with a cancel request
                // that will try to interrupt runner
                releaseShared(0);
                return;
            }
            if (compareAndSetState(s, RAN)) {
                this.exception = t;
                this.result = null;
                releaseShared(0);
                done();
                return;
            }
        }
    }

    boolean innerCancel(boolean mayInterruptIfRunning) {
        for (; ; ) {
            int s = getState();
            if (ranOrCancelled(s)) {
                return false;
            }
            if (compareAndSetState(s, CANCELLED)) {
                break;
            }
        }
        if (mayInterruptIfRunning) {
            Thread r = this.runner;
            if (r != null) {
                r.interrupt();
            }
        }
        releaseShared(0);
        done();
        return true;
    }

    boolean reset() {
        return compareAndSetState(getState(), 0);
    }

}
