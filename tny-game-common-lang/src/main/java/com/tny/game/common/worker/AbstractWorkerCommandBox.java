/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tny.game.common.worker;

import com.tny.game.common.utils.*;
import com.tny.game.common.worker.command.*;
import org.slf4j.*;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractWorkerCommandBox<C extends Command, CB extends CommandBox<C>> extends WorkerCommandBox<C, CB> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(LogAide.WORKER);

    protected volatile Queue<C> queue;

    private final AtomicBoolean submit = new AtomicBoolean(false);

    @Override
    protected abstract Queue<C> acceptQueue();

    protected Queue<C> getQueue() {
        return this.queue;
    }

    @Override
    protected boolean executeIfCurrent(C command) {
        CommandBoxWorker worker = this.worker;
        if (worker != null && worker.isOnCurrentThread()) {
            executeCommand(command);
        }
        if (!command.isDone()) {
            this.queue.add(command);
            postAcceptIntoQueue(command);
            // 受理结果诚实：下游拒绝时回滚入队并显式失败，不得虚报成功（规格6）
            if (!submitWithResult()) {
                this.queue.remove(command);
                return false;
            }
        }
        postAccept(command);
        return true;
    }

    protected void postAccept(C command) {
    }

    protected void postAcceptIntoQueue(C command) {

    }

    @Override
    public boolean bindWorker(CommandBoxWorker worker) {
        if (this.worker != null) {
            return false;
        }
        synchronized (this) {
            if (this.worker != null) {
                return false;
            }
            this.preBind();
            this.worker = worker;
            this.postBind();
            return true;
        }
    }

    @Override
    public boolean unbindWorker() {
        CommandBoxWorker worker = this.worker;
        if (worker == null) {
            return false;
        }
        synchronized (this) {
            if (this.worker != worker) {
                return false;
            }
            this.preUnbind();
            this.worker = null;
            this.postUnbind();
            for (CB box : boxes())
                box.unbindWorker();
            return true;
        }
    }

    protected void preBind() {
    }

    protected void preUnbind() {
    }

    protected void postBind() {
    }

    protected void postUnbind() {
    }

    @Override
    public void clear() {
        this.queue.clear();
    }

    @Override
    public boolean isEmpty() {
        if (!this.queue.isEmpty()) {
            return false;
        }
        for (CommandBox<?> box : boxes()) {
            if (!box.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int size() {
        return this.queue.size();
    }

    @Override
    public void submit() {
        submitWithResult();
    }

    /**
     * @return false 仅当已绑定工作器且唤醒被拒（下游关闭/拒绝）；未绑定=滞留待恢复，视为受理成功。
     */
    private boolean submitWithResult() {
        if (isEmpty()) {
            return true;
        }
        if (!this.submit.compareAndSet(false, true)) {
            // 已有一轮排空承诺负责（排空后的复查闭环保证有限轮次）
            return true;
        }
        CommandBoxWorker worker = this.worker;
        if (worker == null) {
            this.submit.set(false);
            return true;
        }
        // 下游已关闭（终态）：受理诚实失败——调用方回滚入队，不得滞留到永不存在的恢复（规格6）
        if (worker.isShutdown()) {
            this.submit.set(false);
            return false;
        }
        if (!worker.isWorking()) {
            // 工作器停止（未关闭）：命令滞留盒内不丢失、不虚报失败；恢复经心跳/重注册再提交（规格5）
            this.submit.set(false);
            return true;
        }
        try {
            worker.wakeUp(this);
            return true;
        } catch (Exception e) {
            this.submit.set(false);
            return false;
        }
    }

    @Override
    public void wakeUp(CommandBox<?> commandBox) {
        this.worker.wakeUp(this);
    }

    @Override
    public void process() {
        // 清标志与受理竞态闭环：上一轮有进展且仍有存量（清标志前后新受理）时持标自驱下一轮
        // ——不丢唤醒且不依赖外部再提交（规格5）；无进展轮次（如全部延迟未到点）停手，
        // 把驱动权交还唤醒/心跳，避免对未到点命令空转忙等
        do {
            try {
                doProcess();
            } finally {
                this.submit.set(false);
            }
        } while (this.runSize > 0 && !isEmpty() && this.submit.compareAndSet(false, true));
    }

    protected void doProcess() {
    }

    public AbstractWorkerCommandBox(Queue<C> queue) {
        super();
        this.queue = queue;
    }

    protected void executeCommand(C command) {
        command.execute();
    }

}
