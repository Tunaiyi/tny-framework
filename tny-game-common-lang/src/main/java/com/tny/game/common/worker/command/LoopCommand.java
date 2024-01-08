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

public abstract class LoopCommand extends DelayCommand {

    public static final long STOP_LOOP = -1;

    public LoopCommand(String name) {
        this(name, System.currentTimeMillis());
    }

    public LoopCommand(String name, int delay) {
        this(name, System.currentTimeMillis() + delay);
    }

    public LoopCommand(String name, long executeTime) {
        super(name, executeTime);
    }

    @Override
    public void action() {
        try {
            run();
            long delay = nextInterval();
            if (delay <= STOP_LOOP) {
                executed = true;
            } else {
                delay(delay);
                executed = false;
            }
        } catch (Exception e) {
            long delay = nextInterval();
            if (delay <= STOP_LOOP) {
                executed = true;
            } else {
                delay(delay);
                executed = false;
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * 执行任务
     */
    protected abstract void run();

    /**
     * 返回下一次间隔时间
     * 若
     * 返回 STOP_LOOP = -1 会话任务
     * 返回 n 表示 n 毫秒后会再次执行任务;
     *
     * @return
     */
    protected abstract long nextInterval();

}
