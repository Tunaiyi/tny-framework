///*
// * Copyright (c) 2020 Tunaiyi
// * Tny Framework is licensed under Mulan PSL v2.
// * You can use this software according to the terms and conditions of the Mulan PSL v2.
// * You may obtain a copy of Mulan PSL v2 at:
// *          http://license.coscl.org.cn/MulanPSL2
// * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
// NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
// * See the Mulan PSL v2 for more details.
// */
//
//package com.tny.game.net.netty4.processor.disruptor;
//
//import com.tny.game.common.collection.map.access.*;
//import com.tny.game.common.utils.*;
//import org.apache.commons.lang3.*;
//
///**
// * <p>
// *
// * @author : kgtny
// * @date : 2021/7/15 1:35 上午
// */
//public class DisruptorEndpointCommandTaskBoxProcessorSetting {
//
//    /**
//     * 间歇时间
//     */
//    private static final int DEFAULT_THREADS = Runtime.getRuntime().availableProcessors();
//
//    /**
//     * 时间
//     */
//    private static final int DEFAULT_QUEUE_SIZE = 1024 * 1024;
//
//    /**
//     * 间歇时间
//     */
//    private static final int[] DEFAULT_COMMAND_TICK_TIME = {1, 1, 1, 2, 2, 3, 3, 4, 4, 5, 6, 7, 8, 9, 10};
//
//    private boolean enable;
//
//    /* 线程数 */
//    private int threads = DEFAULT_THREADS;
//
//    /* 线程数 */
//    private int queueSize = DEFAULT_QUEUE_SIZE;
//
//    /* 任务调度间隔序列 */
//    private int[] commandTickTimeList = DEFAULT_COMMAND_TICK_TIME;
//
//    /* ChildExecutor command 未完成, 延迟时间*/
//    private DisruptorWaitStrategy waitStrategy = DisruptorWaitStrategy.BLOCKING;
//
//    private ObjectMap waitStrategyProperty = new ObjectMap();
//
//    public int getThreads() {
//        return this.threads;
//    }
//
//    public DisruptorEndpointCommandTaskBoxProcessorSetting setThreads(int threads) {
//        this.threads = threads;
//        return this;
//    }
//
//    public int getQueueSize() {
//        return this.queueSize;
//    }
//
//    public DisruptorEndpointCommandTaskBoxProcessorSetting setQueueSize(int queueSize) {
//        this.queueSize = queueSize;
//        return this;
//    }
//
//    public int[] getCommandTickTimeList() {
//        return this.commandTickTimeList;
//    }
//
//    public DisruptorEndpointCommandTaskBoxProcessorSetting setCommandTickTimeList(int[] commandTickTimeList) {
//        if (ArrayUtils.isNotEmpty(commandTickTimeList)) {
//            for (int value : commandTickTimeList) {
//                Asserts.checkArgument(value > 0, "illegal argument commandTickTimeList [{}]", StringUtils.join(",", commandTickTimeList));
//            }
//            this.commandTickTimeList = commandTickTimeList;
//        }
//        return this;
//    }
//
//    public DisruptorWaitStrategy getWaitStrategy() {
//        return this.waitStrategy;
//    }
//
//    public DisruptorEndpointCommandTaskBoxProcessorSetting setWaitStrategy(DisruptorWaitStrategy waitStrategy) {
//        this.waitStrategy = waitStrategy;
//        return this;
//    }
//
//    public ObjectMap getWaitStrategyProperty() {
//        return this.waitStrategyProperty;
//    }
//
//    public DisruptorEndpointCommandTaskBoxProcessorSetting setWaitStrategyProperty(ObjectMap waitStrategyProperty) {
//        this.waitStrategyProperty = waitStrategyProperty;
//        return this;
//    }
//
//    public boolean isEnable() {
//        return this.enable;
//    }
//
//    public DisruptorEndpointCommandTaskBoxProcessorSetting setEnable(boolean enable) {
//        this.enable = enable;
//        return this;
//    }
//
//}