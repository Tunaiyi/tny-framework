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

import com.google.common.collect.ImmutableList;
import com.tny.game.common.utils.*;
import com.tny.game.common.worker.command.*;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class WorkerCommandBox<C extends Command, CB extends CommandBox<C>> implements CommandBox<C>, CommandBoxWorker {

    protected static final Logger LOGGER = LoggerFactory.getLogger(LogAide.WORKER);

    protected volatile CommandBoxWorker worker;

    protected long runUseTime;

    protected int runSize;

    private volatile Queue<CB> commandBoxList;

    protected abstract Queue<C> acceptQueue();

    protected abstract boolean executeIfCurrent(C command);

    @Override
    public boolean accept(C command) {
        return executeIfCurrent(command);
    }

    public long getProcessUseTime() {
        return this.runUseTime;
    }

    public int getProcessSize() {
        return this.runSize;
    }

    @Override
    public boolean isOnCurrentThread() {
        // 原实现丢弃委托结果恒 false，"当前线程直执行"优化永不可达
        CommandBoxWorker worker = this.worker;
        return worker != null && worker.isOnCurrentThread();
    }

    protected Collection<CB> boxes() {
        Queue<CB> boxes = this.commandBoxList;
        if (boxes != null) {
            return boxes;
        }
        return ImmutableList.of();
    }

    protected Queue<CB> createAndGetBox() {
        Queue<CB> boxes = this.commandBoxList;
        if (boxes != null) {
            return boxes;
        }
        synchronized (this) {
            // 双检：原实现进锁即无条件新建覆盖，并发首建丢子盒
            if (this.commandBoxList == null) {
                this.commandBoxList = new ConcurrentLinkedQueue<>();
            }
            return this.commandBoxList;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean register(CommandBox<?> commandBox) {
        if (commandBox.bindWorker(this)) {
            createAndGetBox().add((CB) commandBox);
            return true;
        }
        return false;
    }

    @Override
    public boolean unregister(CommandBox<?> commandBox) {
        Collection<? extends CommandBox<?>> boxes = boxes();
        if (boxes.remove(commandBox)) {
            return commandBox.unbindWorker();
        }
        return false;
    }

    @Override
    public boolean isWorking() {
        return this.worker != null && this.worker.isWorking();
    }

}
