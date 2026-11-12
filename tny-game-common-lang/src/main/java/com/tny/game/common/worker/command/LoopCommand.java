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
            reschedule();
        } catch (Exception e) {
            // 异常轮同样重排（规格：异常可观测但循环不得静默停摆），异常继续上抛
            reschedule();
            throw new RuntimeException(e);
        }
    }

    private void reschedule() {
        long delay = nextInterval();
        if (delay <= STOP_LOOP) {
            // 终止信号：标记结束并退出调度
            this.executed = true;
        } else {
            delay(delay);
            markRescheduled();
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
