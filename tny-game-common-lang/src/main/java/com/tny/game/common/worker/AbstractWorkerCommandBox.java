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
            this.submit();
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
        try {
            if (!this.isEmpty() && this.submit.compareAndSet(false, true)) {
                this.worker.wakeUp(this);
            }
        } catch (Exception e) {
            this.submit.set(false);
        }
    }

    @Override
    public void wakeUp(CommandBox<?> commandBox) {
        this.worker.wakeUp(this);
    }

    @Override
    public void process() {
        try {
            doProcess();
        } finally {
            this.submit.set(false);
        }
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
