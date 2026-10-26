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
