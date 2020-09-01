package com.tny.game.common.runtime;

import com.tny.game.common.utils.*;
import org.slf4j.*;

import java.util.*;

import static com.tny.game.common.utils.StringAide.*;

public class RunningChecker {

    public static final Logger LOGGER = LoggerFactory.getLogger(RunningChecker.class);

    public static class RunnerCounter {

        private Object task;
        private long startAt;
        private long endAt = -1;

        private RunnerCounter(Object task, long startAt) {
            super();
            this.task = task;
            this.startAt = startAt;
        }

        public Object getTask() {
            return this.task;
        }

        public long getStartAt() {
            return this.startAt;
        }

        public long getEndAt() {
            return this.endAt;
        }

        private void end() {
            if (this.endAt == -1) {
                this.endAt = System.currentTimeMillis();
            }
        }

        public long cost() {
            return this.endAt - this.startAt;
        }

    }

    private static ThreadLocal<Map<Object, RunnerCounter>> timeLocal = new ThreadLocal<>();

    private static RunnerCounter removeCounter(Object object) {
        Map<Object, RunnerCounter> counterMap = timeLocal.get();
        if (counterMap == null) {
            return null;
        }
        return counterMap.remove(object);
    }

    public static RunnerCounter doStart(Object task) {
        long time = System.currentTimeMillis();
        Map<Object, RunnerCounter> counterMap = timeLocal.get();
        if (counterMap == null) {
            counterMap = new HashMap<>();
            timeLocal.set(counterMap);
        }
        RunnerCounter counter = counterMap.get(task);
        if (counter != null) {
            LOGGER.warn("{} 任务没有结束!!", task);
        }
        counter = new RunnerCounter(task, time);
        counterMap.put(task, counter);
        return counter;
    }

    public static void start(Object task) {
        doStart(task);
    }

    public static void startPrint(Object task) {
        startPrint(task, null);
    }

    public static void startPrint(Object task, String message, Object... args) {
        RunnerCounter counter = doStart(task);
        String log = null;
        if (counter.getTask() != null || message != null) {
            log = new Date(counter.getStartAt()) + " OO 开始运行 " + task;
            LOGGER.debug(log + "\n\t" + ObjectAide.ifNull(message, ""), args);
        }
    }

    public static RunnerCounter end(Object task) {
        RunnerCounter counter = removeCounter(task);
        if (counter == null) {
            throw new NullPointerException(format("未开始计算! {} ", task));
        }
        counter.end();
        return counter;
    }

    public static boolean hasCheck(Object task) {
        Map<Object, RunnerCounter> counterMap = timeLocal.get();
        if (counterMap == null) {
            return false;
        }
        return counterMap.containsKey(task);
    }

    public static void endPrint(Object task) {
        endPrint(task, null);
    }

    public static void endPrint(Object task, String message, Object... args) {
        RunnerCounter counter = end(task);
        String log = null;
        log = new Date(counter.getEndAt()) + " XX 结束运行" + (task == null ? "" : " " + task + " ") + " | 消耗 " + (counter.cost()) + " ms";
        LOGGER.debug(log + "\n\t" + ObjectAide.ifNull(message, ""), args);
    }

}
