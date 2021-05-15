package com.tny.game.common.runtime;

import com.tny.game.common.concurrent.*;
import com.tny.game.common.utils.*;
import org.slf4j.*;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/10 5:35 下午
 */
public class ProcessWatcher {

    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = ThreadPoolExecutors.scheduled("ProcessWatcherScheduled", 1);

    private static final TrackPrintOption DEFAULT_PRINT_OPTION = TrackPrintOption.SETTLE;
    public static final Logger LOGGER = LoggerFactory.getLogger(ProcessWatcher.class);

    private static final Map<Object, ProcessWatcher> trackerMap = new ConcurrentHashMap<>();
    private static final Consumer<ProcessWatcher> NONE = null;

    private final Object target;
    private final Logger logger;
    private long cycle;
    private final TrackPrintOption printOption;
    private final AtomicLong traceIdCreator = new AtomicLong();
    private final LongAdder totalCostTime = new LongAdder();
    private final LongAdder totalDoneTimes = new LongAdder();
    private final LongAdder totalTraceTimes = new LongAdder();
    private final LongAdder cycleCostTime = new LongAdder();
    private final LongAdder cycleDoneTimes = new LongAdder();
    private final LongAdder cycleTraceTimes = new LongAdder();
    private volatile ScheduledFuture<?> scheduledFuture;

    public static ProcessWatcher of(Object target) {
        return of(target, NONE);
    }

    public static ProcessWatcher of(Object target, Consumer<ProcessWatcher> onInitiate) {
        return trackerMap.computeIfAbsent(target, create(ProcessWatcher::new, onInitiate));
    }

    public static ProcessWatcher of(Object target, Logger logger) {
        return of(target, logger, NONE);
    }

    public static ProcessWatcher of(Object target, Logger logger, Consumer<ProcessWatcher> onInitiate) {
        return trackerMap.computeIfAbsent(target, create((c) -> new ProcessWatcher(target, logger, DEFAULT_PRINT_OPTION), onInitiate));
    }

    public static ProcessWatcher of(Object target, TrackPrintOption printOption) {
        return of(target, printOption, NONE);
    }

    public static ProcessWatcher of(Object target, TrackPrintOption printOption, Consumer<ProcessWatcher> onInitiate) {
        return trackerMap.computeIfAbsent(target, create((c) -> new ProcessWatcher(target, LOGGER, printOption), onInitiate));
    }

    public static ProcessWatcher of(Object target, Logger logger, TrackPrintOption printOption) {
        return of(target, logger, printOption, NONE);
    }

    public static ProcessWatcher of(Object target, Logger logger, TrackPrintOption printOption, Consumer<ProcessWatcher> onInitiate) {
        return trackerMap.computeIfAbsent(target, create((c) -> new ProcessWatcher(target, logger, printOption), onInitiate));
    }

    private static Function<Object, ProcessWatcher> create(Function<Object, ProcessWatcher> creator, Consumer<ProcessWatcher> onInitiate) {
        if (onInitiate == null) {
            return creator;
        }
        return (c) -> {
            ProcessWatcher watcher = creator.apply(c);
            onInitiate.accept(watcher);
            return watcher;
        };
    }

    private ProcessWatcher(Object target) {
        this(target, LOGGER, TrackPrintOption.SETTLE);
    }

    private ProcessWatcher(Object target, Logger logger, TrackPrintOption printOption) {
        this.target = target;
        this.logger = logger;
        this.printOption = printOption;
    }

    public ProcessTracer trace() {
        return trace(null, this.printOption);
    }

    public ProcessTracer trace(String id) {
        return trace(id, this.printOption);
    }

    public ProcessTracer trace(String id, TrackPrintOption printOption) {
        return createCost(id, printOption).start();
    }

    public ProcessTracer trace(String id, String message, Object... params) {
        return createCost(id, TrackPrintOption.ALL).start(message, params);
    }

    public void statisticsLog() {
        long totalCostTime = this.totalCostTime.longValue();
        long totalDoneTimes = this.totalDoneTimes.longValue();
        LogFragment logFragment = LogFragment.message("执行监控 [ {} ]  总执行统计 | 总执行次数 {}; 总完成次数 {}; 总消耗时间 {} us; 平均耗时 {} us | ",
                this.target, this.totalTraceTimes.longValue(), totalDoneTimes, totalCostTime,
                totalDoneTimes == 0 ? 0 : totalCostTime / totalDoneTimes);
        ScheduledFuture<?> future = this.scheduledFuture;
        if (future != null) {
            long cycleCostTime = this.cycleCostTime.longValue();
            long cycleDoneTimes = this.cycleDoneTimes.longValue();
            logFragment.append("周期({} s)执行统计 | 执行次数 {}; 完成次数 {}; 消耗时间 {} us; 平均耗时 {} us",
                    this.cycle, this.cycleTraceTimes.longValue(), cycleDoneTimes, cycleCostTime,
                    cycleDoneTimes == 0 ? 0 : cycleCostTime / cycleDoneTimes);
        }
        logFragment.log(this.logger);
    }

    private void cycle() {
        statisticsLog();
        this.cycleTraceTimes.reset();
        this.cycleCostTime.reset();
        this.cycleDoneTimes.reset();
    }

    public ProcessWatcher stopSchedule() {
        synchronized (this) {
            if (this.scheduledFuture != null) {
                this.scheduledFuture.cancel(false);
                this.scheduledFuture = null;
            }
        }
        return this;
    }

    public ProcessWatcher schedule(long delay, TimeUnit unit) {
        synchronized (this) {
            if (this.scheduledFuture != null) {
                this.scheduledFuture.cancel(false);
            }
            this.cycle = unit.toSeconds(delay);
            this.scheduledFuture = SCHEDULED_EXECUTOR_SERVICE
                    .scheduleAtFixedRate(this::cycle, delay, delay, unit);
        }
        return this;
    }

    private void onDone(ProcessTracer motion) {
        this.cycleDoneTimes.add(1);
        this.totalDoneTimes.add(1);
        long cost = motion.costTime();
        this.cycleCostTime.add(cost);
        this.totalCostTime.add(cost);
    }

    private ProcessTracer createCost(String id, TrackPrintOption printOption) {
        if (StringAide.isBlank(id)) {
            long index = this.traceIdCreator.incrementAndGet();
            id = String.valueOf(index);
        }
        ProcessTracer motion = new ProcessTracer(this.target, id, this.logger, printOption, this::onDone);
        this.cycleTraceTimes.add(1);
        this.totalTraceTimes.add(1);
        return motion;
    }

}
