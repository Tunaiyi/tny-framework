/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.command.processor.forkjoin;

/**
 * @author KGTny
 */
public class ForkJoinEndpointCommandTaskProcessorSetting {

    /**
     * 间歇时间
     */
    private static final int DEFAULT_NEXT_INTERVAL = 8;

    /**
     * 间歇时间
     */
    private static final int DEFAULT_THREADS = Runtime.getRuntime().availableProcessors();

    /* 线程数 */
    private int threads = DEFAULT_THREADS;

    /* ChildExecutor command 未完成, 延迟时间*/
    private int nextInterval = DEFAULT_NEXT_INTERVAL;

    private boolean enable = true;

    public int getThreads() {
        return this.threads;
    }

    public ForkJoinEndpointCommandTaskProcessorSetting setThreads(int threads) {
        this.threads = threads;
        return this;
    }

    public int getNextInterval() {
        return this.nextInterval;
    }

    public ForkJoinEndpointCommandTaskProcessorSetting setNextInterval(int nextInterval) {
        this.nextInterval = nextInterval;
        return this;
    }

    public boolean isEnable() {
        return this.enable;
    }

    public ForkJoinEndpointCommandTaskProcessorSetting setEnable(boolean enable) {
        this.enable = enable;
        return this;
    }

}
