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
package com.tny.game.net.command.processor.forkjoin;

/**
 * @author KGTny
 */
public class SerialCommandExecutorSetting {

    /**
     * 间歇时间
     */
    private static final int DEFAULT_THREADS = Runtime.getRuntime().availableProcessors();

    /* 线程数 */
    private int threads = DEFAULT_THREADS;

    private boolean enable = true;

    // 异步命令兜底时限（毫秒）：业务返回的 CompletionStage 超期未完成即错误应答并推进串行队列
    private long commandTimeout = 3000L;

    public int getThreads() {
        return this.threads;
    }

    public SerialCommandExecutorSetting setThreads(int threads) {
        this.threads = threads;
        return this;
    }

    public boolean isEnable() {
        return this.enable;
    }

    public long getCommandTimeout() {
        return commandTimeout;
    }

    public SerialCommandExecutorSetting setCommandTimeout(long commandTimeout) {
        this.commandTimeout = commandTimeout;
        return this;
    }

    public SerialCommandExecutorSetting setEnable(boolean enable) {
        this.enable = enable;
        return this;
    }

}
