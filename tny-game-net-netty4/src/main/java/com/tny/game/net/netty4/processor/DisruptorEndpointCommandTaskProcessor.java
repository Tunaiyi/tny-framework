package com.tny.game.net.netty4.processor;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.ProducerType;
import com.tny.game.common.collection.map.access.*;
import com.tny.game.common.concurrent.*;
import com.tny.game.common.lifecycle.*;
import com.tny.game.net.command.processor.*;
import org.slf4j.*;

import java.util.Map;
import java.util.concurrent.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/20 2:06 下午
 */
public class DisruptorEndpointCommandTaskProcessor extends EndpointCommandTaskProcessor implements AppPrepareStart {

    public static final Logger LOGGER = LoggerFactory.getLogger(DisruptorEndpointCommandTaskProcessor.class);

    /**
     * 间歇时间
     */
    private static final int DEFAULT_NEXT_INTERVAL = 8;
    /**
     * 间歇时间
     */
    private static final int DEFAULT_THREADS = Runtime.getRuntime().availableProcessors();
    /**
     * 间歇时间
     */
    private static final int DEFAULT_QUEUE_SIZE = 1024 * 1024;

    /* 线程数 */
    private int threads = DEFAULT_THREADS;

    /* 线程数 */
    private int queueSize = DEFAULT_QUEUE_SIZE;

    /* ChildExecutor command 未完成, 延迟时间*/
    private int nextInterval = DEFAULT_NEXT_INTERVAL;

    /* ChildExecutor command 未完成, 延迟时间*/
    private DisruptorWaitStrategy waitStrategy = DisruptorWaitStrategy.BLOCKING;

    private ObjectMap waitStrategyProperty = new ObjectMap();

    private RingBuffer<BufferItem<BoxProcessor>> boxProcessorBuffer;

    private ExecutorService executorService;

    private final ScheduledExecutorService scheduledExecutorService = ThreadPoolExecutors
            .scheduled(this.getClass().getSimpleName() + "Scheduled", 1, true);

    public DisruptorEndpointCommandTaskProcessor() {
        this(Runtime.getRuntime().availableProcessors(), DEFAULT_NEXT_INTERVAL);
    }

    public DisruptorEndpointCommandTaskProcessor(int threads) {
        this(threads, DEFAULT_NEXT_INTERVAL);
    }

    public DisruptorEndpointCommandTaskProcessor(int threads, int nextInterval) {
        this.threads = threads;
        this.nextInterval = nextInterval;
    }

    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), LifecycleLevel.SYSTEM_LEVEL_7);
    }

    @Override
    public void prepareStart() {
        this.boxProcessorBuffer = createRingBuffer();
        //3 创建含有10个消费者的数组:
        BoxProcessorConsumer[] consumers = new BoxProcessorConsumer[this.threads];
        for (int i = 0; i < consumers.length; i++) {
            consumers[i] = new BoxProcessorConsumer("BoxProcessorConsumer-" + i);
        }
        WorkerPool<BufferItem<BoxProcessor>> workerPool = createWorkerPool(this.boxProcessorBuffer, consumers);
        this.executorService = Executors.newCachedThreadPool(new CoreThreadFactory("BoxProcessorConsumer"));
        LOGGER.info("Create WorkerPool with {} threads, {} queueSize, {} waitStrategy by properties {}",
                this.threads, this.queueSize, this.waitStrategy, this.waitStrategyProperty);
        workerPool.start(this.executorService);
        this.scheduledExecutorService.scheduleAtFixedRate(this::scheduleProcessor, this.nextInterval, this.nextInterval, TimeUnit.MILLISECONDS);
    }

    private RingBuffer<BufferItem<BoxProcessor>> createRingBuffer() {
        return RingBuffer.create(ProducerType.MULTI, BufferItem::new,
                this.queueSize, this.waitStrategy.createStrategy(this.waitStrategyProperty));
    }

    private WorkerPool<BufferItem<BoxProcessor>> createWorkerPool(RingBuffer<BufferItem<BoxProcessor>> ringBuffer, BoxProcessorConsumer[] consumers) {
        WorkerPool<BufferItem<BoxProcessor>> pool = new WorkerPool<>(ringBuffer,
                // 通过ringBuffer 创建一个屏障
                ringBuffer.newBarrier(), new BoxProcessorExceptionHandler(), consumers);
        ringBuffer.addGatingSequences(pool.getWorkerSequences());
        return pool;

    }

    public void shutdown() {
        this.executorService.shutdown();
    }

    @Override
    protected void execute(BoxProcessor processor) {
        long sequence = this.boxProcessorBuffer.next();
        try {
            BufferItem<BoxProcessor> item = this.boxProcessorBuffer.get(sequence);
            item.setItem(processor);
        } finally {
            this.boxProcessorBuffer.publish(sequence);
        }
    }

    public DisruptorEndpointCommandTaskProcessor setWaitStrategyProperty(Map<String, Object> waitStrategyProperty) {
        this.waitStrategyProperty = new ObjectMap(waitStrategyProperty);
        return this;
    }

    public DisruptorEndpointCommandTaskProcessor setWaitStrategy(DisruptorWaitStrategy waitStrategy) {
        this.waitStrategy = waitStrategy;
        return this;
    }

    public DisruptorEndpointCommandTaskProcessor setQueueSize(int queueSize) {
        this.queueSize = queueSize;
        return this;
    }

    public DisruptorEndpointCommandTaskProcessor setThreads(int threads) {
        this.threads = threads;
        return this;
    }

    public DisruptorEndpointCommandTaskProcessor setNextInterval(int nextInterval) {
        this.nextInterval = nextInterval;
        return this;
    }

    private static class BoxProcessorExceptionHandler implements ExceptionHandler<BufferItem<BoxProcessor>> {

        @Override
        public void handleEventException(Throwable ex, long sequence, BufferItem<BoxProcessor> event) {
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

    private static class BoxProcessorConsumer implements WorkHandler<BufferItem<BoxProcessor>> {

        private String name;

        private BoxProcessorConsumer(String name) {
            this.name = name;
        }

        @Override
        public void onEvent(BufferItem<BoxProcessor> event) {
            BoxProcessor processor = event.getItem();
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
