package com.tny.game.net.netty4.processor.disruptor;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.ProducerType;
import com.tny.game.common.concurrent.*;
import com.tny.game.common.lifecycle.*;
import com.tny.game.net.command.processor.*;
import com.tny.game.net.endpoint.task.*;
import io.netty.util.HashedWheelTimer;
import org.slf4j.*;

import java.util.concurrent.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/20 2:06 下午
 */
public class DisruptorEndpointCommandTaskProcessor extends EndpointCommandTaskProcessor<TimerEndpointCommandTaskTrigger> implements AppPrepareStart {

    public static final Logger LOGGER = LoggerFactory.getLogger(DisruptorEndpointCommandTaskProcessor.class);

    /* 线程数 */
    private final DisruptorEndpointCommandTaskProcessorSetting setting;

    private final int[] commandTickTimeList;

    private RingBuffer<BufferItem<EndpointCommandTaskTrigger>> boxProcessorBuffer;

    private ExecutorService executorService;

    private static final HashedWheelTimer TIMER = new HashedWheelTimer(
            new CoreThreadFactory("DisruptorEndpointCommandTaskTimer"), 1, TimeUnit.MILLISECONDS);

    public DisruptorEndpointCommandTaskProcessor() {
        this(new DisruptorEndpointCommandTaskProcessorSetting());
    }

    public DisruptorEndpointCommandTaskProcessor(DisruptorEndpointCommandTaskProcessorSetting setting) {
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
            consumers[i] = new BoxProcessorConsumer("BoxProcessorConsumer-" + i);
        }
        WorkerPool<BufferItem<EndpointCommandTaskTrigger>> workerPool = createWorkerPool(this.boxProcessorBuffer, consumers);
        this.executorService = Executors.newCachedThreadPool(new CoreThreadFactory("BoxProcessorConsumer"));
        LOGGER.info("Create WorkerPool with {} threads, {} queueSize, {} waitStrategy by properties {}",
                this.setting.getThreads(), this.setting.getQueueSize(), this.setting.getQueueSize(), this.setting.getWaitStrategy());
        workerPool.start(this.executorService);
        TIMER.start();
    }

    private RingBuffer<BufferItem<EndpointCommandTaskTrigger>> createRingBuffer() {
        return RingBuffer.create(ProducerType.MULTI, BufferItem::new,
                this.setting.getThreads(), this.setting.getWaitStrategy().createStrategy(this.setting.getWaitStrategyProperty()));
    }

    private WorkerPool<BufferItem<EndpointCommandTaskTrigger>> createWorkerPool(RingBuffer<BufferItem<EndpointCommandTaskTrigger>> ringBuffer,
            BoxProcessorConsumer[] consumers) {
        WorkerPool<BufferItem<EndpointCommandTaskTrigger>> pool = new WorkerPool<>(ringBuffer,
                // 通过ringBuffer 创建一个屏障
                ringBuffer.newBarrier(), new BoxProcessorExceptionHandler(), consumers);
        ringBuffer.addGatingSequences(pool.getWorkerSequences());
        return pool;

    }

    public void shutdown() {
        this.executorService.shutdown();
    }

    @Override
    protected TimerEndpointCommandTaskTrigger createTrigger(CommandTaskBox box) {
        return new TimerEndpointCommandTaskTrigger(box, this);
    }

    @Override
    protected void process(TimerEndpointCommandTaskTrigger processor) {
        long sequence = this.boxProcessorBuffer.next();
        try {
            BufferItem<EndpointCommandTaskTrigger> item = this.boxProcessorBuffer.get(sequence);
            item.setItem(processor);
        } finally {
            this.boxProcessorBuffer.publish(sequence);
        }
    }

    @Override
    protected void schedule(TimerEndpointCommandTaskTrigger processor) {
        int times = processor.getCommandTickTimes();
        long delay;
        if (times < this.commandTickTimeList.length) {
            delay = this.commandTickTimeList[times];
        } else {
            delay = this.lastCommandTickTime();
        }
        TIMER.newTimeout(processor, delay, TimeUnit.MILLISECONDS);
    }

    private long lastCommandTickTime() {
        return this.commandTickTimeList[this.commandTickTimeList.length - 1];
    }

    private static class BoxProcessorExceptionHandler implements ExceptionHandler<BufferItem<EndpointCommandTaskTrigger>> {

        @Override
        public void handleEventException(Throwable ex, long sequence, BufferItem<EndpointCommandTaskTrigger> event) {
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

    private static class BoxProcessorConsumer implements WorkHandler<BufferItem<EndpointCommandTaskTrigger>> {

        private final String name;

        private BoxProcessorConsumer(String name) {
            this.name = name;
        }

        @Override
        public void onEvent(BufferItem<EndpointCommandTaskTrigger> event) {
            EndpointCommandTaskTrigger processor = event.getItem();
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