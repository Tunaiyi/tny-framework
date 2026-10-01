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

package com.tny.game.common.worker.command;

public abstract class DelayCommand implements Command {

    private volatile long executeTime;

    protected final String name;

    protected boolean executed = false;

    public DelayCommand() {
        this(null, System.currentTimeMillis());
    }

    public DelayCommand(String name) {
        this(name, System.currentTimeMillis());
    }

    public DelayCommand(String name, int delay) {
        this(name, System.currentTimeMillis() + delay);
    }

    public DelayCommand(String name, long executeTime) {
        this.name = name;
        this.executeTime = executeTime;
    }

    /**
     * action 内是否已为后续轮次重排（LoopCommand 专用）：
     * 原实现 finally 无条件 executed=true 会把重排覆写成单轮终止。
     */
    private boolean rescheduled = false;

    @Override
    public void execute() {
        // 原判据取反（isCanExecute() 为真反而 return）：未到点的新命令被立即执行
        if (!isCanExecute()) {
            return;
        }
        this.rescheduled = false;
        try {
            this.action();
        } catch (Exception exception) {
            if (!this.rescheduled) {
                this.executed = true;
            }
            throw new RuntimeException(exception);
        }
        if (!this.rescheduled) {
            this.executed = true;
        }
    }

    /**
     * 子类（循环命令）在 action 内完成重排时调用，声明"本轮结束但命令未完"。
     */
    protected final void markRescheduled() {
        this.rescheduled = true;
    }

    protected abstract void action();

    @Override
    public String getName() {
        if (this.name == null) {
            return Command.super.getName();
        }
        return name;
    }

    protected void immediately() {
        this.executeTime = System.currentTimeMillis();
    }

    protected void delay(long delay) {
        this.executeTime = System.currentTimeMillis() + delay;
    }

    protected void runAt(long time) {
        this.executeTime = time;
    }

    public long getExecuteTime() {
        return executeTime;
    }

    protected long getDelay() {
        long delay = executeTime - System.currentTimeMillis();
        return delay < 0 ? 0L : delay;
    }

    @Override
    public boolean isDone() {
        return this.executed;
    }

    /**
     * 可执行 = 尚未结束 且 执行时点已到（原定义 isDone()&&delay<=0 与执行门联合构成判反）。
     */
    public boolean isCanExecute() {
        return !isDone() && getDelay() <= 0;
    }

}
