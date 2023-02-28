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
package com.tny.game.net.netty4.processor.disruptor;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.ProducerType;
import com.tny.game.common.concurrent.*;
import com.tny.game.common.lifecycle.*;
import com.tny.game.net.command.processor.*;
import io.netty.util.HashedWheelTimer;
import org.slf4j.*;

import java.util.concurrent.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/20 2:06 下午
 */
public class DisruptorEndpointCommandBoxProcessor extends EndpointCommandBoxProcessor<TimerCommandBoxProcess> implements AppPrepareStart {

    public static final Logger LOGGER = LoggerFactory.getLogger(DisruptorEndpointCommandBoxProcessor.class);

    /* 线程数 */
    private final DisruptorEndpointCommandTaskBoxProcessorSetting setting;

    private final int[] commandTickTimeList;

    private RingBuffer<BufferItem<CommandBoxProcess>> boxProcessorBuffer;

    private ExecutorService executorService;

    private static final HashedWheelTimer TIMER = new HashedWheelTimer(
            new CoreThreadFactory("DisruptorEndpointCommandTaskTimer"), 10, TimeUnit.MILLISECONDS);

    public DisruptorEndpointCommandBoxProcessor() {
        this(new DisruptorEndpointCommandTaskBoxProcessorSetting());
    }

    public DisruptorEndpointCommandBoxProcessor(DisruptorEndpointCommandTaskBoxProcessorSetting setting) {
        this.setting = setting;
        this.commandTickTimeList = setting.getCommandTickTimeList();
    }

    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), LifecycleLevel.SYSTEM_LEVEL_7);
    }

    @Override
    public void prepareStart() {
        this.boxProcessorBuffer = createRingBuffer();
        //3 创建含有10个消费者的数组:
        BoxProcessorConsumer[] consumers = new BoxProcessorConsumer[this.setting.getThreads()];
        for (int i = 0; i < consumers.length; i++) {
            //			"BoxProcessorConsumer-" + i
            consumers[i] = new BoxProcessorConsumer();
        }
        WorkerPool<BufferItem<CommandBoxProcess>> workerPool = createWorkerPool(this.boxProcessorBuffer, consumers);
        this.executorService = Executors.newCachedThreadPool(new CoreThreadFactory("BoxProcessorConsumer"));
        LOGGER.info("Create WorkerPool with {} threads, {} queueSize, {} waitStrategy by properties {}",
                this.setting.getThreads(), this.setting.getQueueSize(), this.setting.getQueueSize(), this.setting.getWaitStrategy());
        workerPool.start(this.executorService);
        TIMER.start();
    }

    private RingBuffer<BufferItem<CommandBoxProcess>> createRingBuffer() {
        return RingBuffer.create(ProducerType.MULTI, BufferItem::new,
                this.setting.getThreads(), this.setting.getWaitStrategy().createStrategy(this.setting.getWaitStrategyProperty()));
    }

    private WorkerPool<BufferItem<CommandBoxProcess>> createWorkerPool(RingBuffer<BufferItem<CommandBoxProcess>> ringBuffer,
            BoxProcessorConsumer[] consumers) {
        WorkerPool<BufferItem<CommandBoxProcess>> pool = new WorkerPool<>(ringBuffer,
                // 通过ringBuffer 创建一个屏障
                ringBuffer.newBarrier(), new BoxProcessorExceptionHandler(), consumers);
        ringBuffer.addGatingSequences(pool.getWorkerSequences());
        return pool;

    }

    public void shutdown() {
        this.executorService.shutdown();
    }

    @Override
    protected TimerCommandBoxProcess createDriver(MessageCommandBox box) {
        return new TimerCommandBoxProcess(box, this);
    }

    @Override
    public void handle(TimerCommandBoxProcess driver) {
        boxProcessorBuffer.publishEvent(this::publish, driver);
    }

    private void publish(BufferItem<CommandBoxProcess> item, long sequence, CommandBoxProcess driver) {
        item.setItem(driver);
    }

    @Override
    public void schedule(TimerCommandBoxProcess driver) {
        int times = driver.getCommandTickTimes();
        long delay;
        if (times < this.commandTickTimeList.length) {
            delay = this.commandTickTimeList[times];
        } else {
            delay = this.lastCommandTickTime();
        }
        TIMER.newTimeout(driver, delay, TimeUnit.MILLISECONDS);
    }

    private long lastCommandTickTime() {
        return this.commandTickTimeList[this.commandTickTimeList.length - 1];
    }

    private static class BoxProcessorExceptionHandler implements ExceptionHandler<BufferItem<CommandBoxProcess>> {

        @Override
        public void handleEventException(Throwable ex, long sequence, BufferItem<CommandBoxProcess> event) {
            LOGGER.error("sequence {} BoxProcessor handler error", event);
        }

        @Override
        public void handleOnStartException(Throwable ex) {
            LOGGER.error("WorkerPool start exception", ex);
        }

        @Override
        public void handleOnShutdownException(Throwable ex) {
            LOGGER.error("WorkerPool shutdown exception", ex);
        }

    }

    private static class BoxProcessorConsumer implements WorkHandler<BufferItem<CommandBoxProcess>> {

        private BoxProcessorConsumer() {
        }

        @Override
        public void onEvent(BufferItem<CommandBoxProcess> event) {
            CommandBoxProcess processor = event.getItem();
            try {
                if (processor != null) {
                    processor.run();
                }
            } finally {
                event.reset();
            }
        }

    }

    private static class BufferItem<T> {

        private long sequence;

        private T item;

        private BufferItem() {
        }

        public long getSequence() {
            return this.sequence;
        }

        public T getItem() {
            return this.item;
        }

        private BufferItem<T> setSequence(long sequence) {
            this.sequence = sequence;
            return this;
        }

        private BufferItem<T> setItem(T item) {
            this.item = item;
            return this;
        }

        public void reset() {
            this.sequence = 0;
            this.item = null;
        }

    }

}
