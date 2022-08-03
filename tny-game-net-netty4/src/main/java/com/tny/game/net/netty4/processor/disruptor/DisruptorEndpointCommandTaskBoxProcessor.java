package com.tny.game.net.netty4.processor.disruptor;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.ProducerType;
import com.tny.game.common.concurrent.*;
import com.tny.game.common.lifecycle.*;
import com.tny.game.net.command.processor.*;
import com.tny.game.net.command.task.*;
import io.netty.util.HashedWheelTimer;
import org.slf4j.*;

import java.util.concurrent.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/20 2:06 下午
 */
public class DisruptorEndpointCommandTaskBoxProcessor extends EndpointCommandTaskBoxProcessor<TimerCommandTaskBoxDriver> implements AppPrepareStart {

    public static final Logger LOGGER = LoggerFactory.getLogger(DisruptorEndpointCommandTaskBoxProcessor.class);

    /* 线程数 */
    private final DisruptorEndpointCommandTaskBoxProcessorSetting setting;

    private final int[] commandTickTimeList;

    private RingBuffer<BufferItem<CommandTaskBoxDriver>> boxProcessorBuffer;

    private ExecutorService executorService;

    private static final HashedWheelTimer TIMER = new HashedWheelTimer(
            new CoreThreadFactory("DisruptorEndpointCommandTaskTimer"), 10, TimeUnit.MILLISECONDS);

    public DisruptorEndpointCommandTaskBoxProcessor() {
        this(new DisruptorEndpointCommandTaskBoxProcessorSetting());
    }

    public DisruptorEndpointCommandTaskBoxProcessor(DisruptorEndpointCommandTaskBoxProcessorSetting setting) {
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
        WorkerPool<BufferItem<CommandTaskBoxDriver>> workerPool = createWorkerPool(this.boxProcessorBuffer, consumers);
        this.executorService = Executors.newCachedThreadPool(new CoreThreadFactory("BoxProcessorConsumer"));
        LOGGER.info("Create WorkerPool with {} threads, {} queueSize, {} waitStrategy by properties {}",
                this.setting.getThreads(), this.setting.getQueueSize(), this.setting.getQueueSize(), this.setting.getWaitStrategy());
        workerPool.start(this.executorService);
        TIMER.start();
    }

    private RingBuffer<BufferItem<CommandTaskBoxDriver>> createRingBuffer() {
        return RingBuffer.create(ProducerType.MULTI, BufferItem::new,
                this.setting.getThreads(), this.setting.getWaitStrategy().createStrategy(this.setting.getWaitStrategyProperty()));
    }

    private WorkerPool<BufferItem<CommandTaskBoxDriver>> createWorkerPool(RingBuffer<BufferItem<CommandTaskBoxDriver>> ringBuffer,
            BoxProcessorConsumer[] consumers) {
        WorkerPool<BufferItem<CommandTaskBoxDriver>> pool = new WorkerPool<>(ringBuffer,
                // 通过ringBuffer 创建一个屏障
                ringBuffer.newBarrier(), new BoxProcessorExceptionHandler(), consumers);
        ringBuffer.addGatingSequences(pool.getWorkerSequences());
        return pool;

    }

    public void shutdown() {
        this.executorService.shutdown();
    }

    @Override
    protected TimerCommandTaskBoxDriver createDriver(CommandTaskBox box) {
        return new TimerCommandTaskBoxDriver(box, this);
    }

    @Override
    public void execute(TimerCommandTaskBoxDriver driver) {
        long sequence = this.boxProcessorBuffer.next();
        try {
            BufferItem<CommandTaskBoxDriver> item = this.boxProcessorBuffer.get(sequence);
            item.setItem(driver);
        } finally {
            this.boxProcessorBuffer.publish(sequence);
        }
    }

    @Override
    public void schedule(TimerCommandTaskBoxDriver driver) {
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

    private static class BoxProcessorExceptionHandler implements ExceptionHandler<BufferItem<CommandTaskBoxDriver>> {

        @Override
        public void handleEventException(Throwable ex, long sequence, BufferItem<CommandTaskBoxDriver> event) {
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

    private static class BoxProcessorConsumer implements WorkHandler<BufferItem<CommandTaskBoxDriver>> {

        private BoxProcessorConsumer() {
        }

        @Override
        public void onEvent(BufferItem<CommandTaskBoxDriver> event) {
            CommandTaskBoxDriver processor = event.getItem();
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
