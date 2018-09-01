package test;

import com.tny.game.common.concurrent.*;
import com.tny.game.common.utils.StringAide;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Predicate;

import static org.junit.Assert.*;

/**
 * Created by Kun Yang on 2018/8/25.
 */
public final class TestAide {

    private static int PROCESSORS_NUM = Runtime.getRuntime().availableProcessors() * 2;
    private static int DEFAULT_TASK_NUM = Math.max(PROCESSORS_NUM, 4);

    public static final Logger LOGGER = LoggerFactory.getLogger(TestAide.class);

    private TestAide() {
    }

    public static void assertRunWithException(RunnableWithThrow run, Collection<Class<? extends Throwable>> expectedExceptionClasses) {
        assertRunWithException(null, run, expectedExceptionClasses);
    }

    public static void assertRunWithException(String name, RunnableWithThrow run, Collection<Class<? extends Throwable>> expectedExceptionClasses) {
        try {
            run.run();
            failPass(name);
        } catch (Throwable e) {
            for (Class<?> expectedExceptionClass : expectedExceptionClasses) {
                if (!expectedExceptionClass.isInstance(e))
                    continue;
                assertTrue(true);
                return;
            }
            failWith(name, e);
        }
    }

    @SafeVarargs
    public static void assertRunWithException(RunnableWithThrow run, Class<? extends Throwable>... expectedExceptionClasses) {
        assertRunWithException(run, Arrays.asList(expectedExceptionClasses));
    }

    public static void assertRunWithException(String name, RunnableWithThrow run, Class<? extends Throwable>... expectedExceptionClasses) {
        assertRunWithException(name, run, Arrays.asList(expectedExceptionClasses));
    }


    public static void assertRunWithException(String name, RunnableWithThrow run, Class<? extends Throwable> expectedExceptionClass) {
        try {
            run.run();
            failPass(name);
        } catch (Throwable e) {
            if (expectedExceptionClass.isInstance(e)) {
                assertTrue(true);
            } else {
                failWith(name, e);
            }
        }
    }

    public static void assertRunWithException(RunnableWithThrow run) {
        assertRunWithAnyException(null, run);
    }

    public static void assertRunWithException(RunnableWithThrow run, Predicate<Throwable> predicate) {
        assertRunWithException(run, predicate);
    }

    public static void assertRunWithException(String name, RunnableWithThrow run, Predicate<Throwable> predicate) {
        try {
            run.run();
            failPass(name);
        } catch (Throwable e) {
            if (predicate.test(e))
                assertTrue(true);
            else
                failWith(name, e);
        }
    }

    public static <T extends Throwable> void assertRunWithException(RunnableWithThrow run, Class<T> expectedExceptionClass, Predicate<T> predicate) {
        assertRunWithException(null, run, expectedExceptionClass, predicate);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Throwable> void assertRunWithException(String name, RunnableWithThrow run, Class<T> expectedExceptionClass, Predicate<T> predicate) {
        try {
            run.run();
            failPass(name);
        } catch (Throwable e) {
            if (expectedExceptionClass.isInstance(e)) {
                if (predicate.test((T) e)) {
                    assertTrue(true);
                    return;
                }
            }
            failWith(name, e);
        }
    }

    public static void assertRunWithAnyException(RunnableWithThrow run) {
        assertRunWithAnyException(null, run);
    }

    public static void assertRunWithAnyException(String name, RunnableWithThrow run) {
        try {
            run.run();
            failPass(name);
        } catch (Exception e) {
            assertTrue(true);
        } catch (Throwable e) {
            failWith(name, e);
        }
    }

    public static void assertRunWithoutException(RunnableWithThrow run) {
        assertRunWithoutException(null, run);
    }

    public static void assertRunWithoutException(String name, RunnableWithThrow run) {
        try {
            run.run();
        } catch (Throwable e) {
            failWith(name, e);
        }
    }

    public static <T> T assertCallWithoutException(Callable<T> run) {
        return assertCallWithoutException(null, run);
    }

    public static <T> T assertCallWithoutException(String name, Callable<T> run) {
        try {
            return run.call();
        } catch (Throwable e) {
            failWith(name, e);
        }
        return null;
    }

    private static void failPass(String name) {
        if (StringUtils.isBlank(name))
            fail(StringAide.format("assert fail without exception"));
        else
            fail(StringAide.format("[{}] assert fail without exception", name));
    }

    private static void failWith(String name, Throwable e) {
        if (StringUtils.isBlank(name)) {
            fail(StringAide.format("assert fail with other exception {} and message : {}", e.getClass(), e.getMessage()));
        } else {
            fail(StringAide.format("[{}] assert fail with other exception {} and message : {}", name, e.getClass(), e.getMessage()));
        }
    }

    public static void runParallel(RunnableWithThrow task) {
        runParallel(DEFAULT_TASK_NUM, PROCESSORS_NUM, task);
    }

    public static void runParallel(int taskNum, RunnableWithThrow task) {
        runParallel(taskNum, PROCESSORS_NUM, task);
    }

    public static void runParallel(int taskNum, int threadSize, RunnableWithThrow task) {
        runParallel("runParallel", taskNum, threadSize, task);
    }

    public static void runParallel(String name, RunnableWithThrow task) {
        runParallel(name, DEFAULT_TASK_NUM, PROCESSORS_NUM, task);
    }

    public static void runParallel(String name, int taskNum, RunnableWithThrow task) {
        runParallel(name, taskNum, PROCESSORS_NUM, task);
    }

    public static void runParallel(String name, int taskNum, int threadSize, RunnableWithThrow task) {
        ForkJoinPool forkJoinPool = ForkJoinPools.pool(threadSize, name);
        List<ForkJoinTask<?>> joinTasks = new ArrayList<>();
        final CountDownLatch latch = new CountDownLatch(1);
        for (int index = 0; index < taskNum; index++) {
            ForkJoinTask<?> joinTask = forkJoinPool.submit(() -> assertRunWithoutException(name, () -> {
                latch.await();
                task.run();
            }));
            joinTasks.add(joinTask);
        }
        latch.countDown();
        for (ForkJoinTask<?> joinTask : joinTasks) {
            assertCallWithoutException(joinTask::join);
        }
        forkJoinPool.shutdown();
    }

    public static <T> List<T> callParallel(Callable<T> task) {
        return callParallel(null, task);
    }

    public static <T> List<T> callParallel(String name, Callable<T> task) {
        return callParallel(name, DEFAULT_TASK_NUM, PROCESSORS_NUM, task);
    }

    public static <T> List<T> callParallel(int taskNum, Callable<T> task) {
        return callParallel(null, taskNum, PROCESSORS_NUM, task);
    }

    public static <T> List<T> callParallel(String name, int taskNum, Callable<T> task) {
        return callParallel(name, taskNum, PROCESSORS_NUM, task);
    }

    public static <T> List<T> callParallel(int taskNum, int threadSize, Callable<T> task) {
        return callParallel(null, taskNum, threadSize, task);
    }

    public static <T> List<T> callParallel(String name, int taskNum, int threadSize, Callable<T> task) {
        ForkJoinPool forkJoinPool = ForkJoinPools.pool(threadSize, name);
        List<ForkJoinTask<T>> joinTasks = new ArrayList<>();
        final CountDownLatch latch = new CountDownLatch(1);
        for (int index = 0; index < taskNum; index++) {
            String taskName = taskName(name, index);
            ForkJoinTask<T> joinTask = forkJoinPool.submit(() -> assertCallWithoutException(taskName, () -> {
                latch.await();
                return task.call();
            }));
            joinTasks.add(joinTask);
        }
        latch.countDown();
        List<T> results = new ArrayList<>();
        int index = 0;
        for (ForkJoinTask<T> joinTask : joinTasks) {
            String taskName = taskName(name, index++);
            T result = assertCallWithoutException(taskName, joinTask::get);
            results.add(result);
        }
        forkJoinPool.shutdown();
        return results;
    }

    public static TestTask<?>[] parallelTask(TestTask<?>... tasks) {
        return parallelTask(PROCESSORS_NUM, tasks);
    }

    public static TestTask<?>[] parallelTask(int threadSize, TestTask<?>... tasks) {
        ForkJoinPool forkJoinPool = ForkJoinPools.pool(threadSize, "parallelTask");
        List<ForkJoinTask<?>> joinTasks = new ArrayList<>();
        final CountDownLatch latch = new CountDownLatch(1);
        for (TestTask<?> task : tasks) {
            for (int index = 0; index < task.getTimes(); index++) {
                String taskName = taskName(task.getName(), index);
                ForkJoinTask<?> joinTask = forkJoinPool.submit(() -> assertCallWithoutException(taskName, () -> {
                    latch.await();
                    return task.getCallable().call();
                }));
                task.addTask(joinTask);
            }
        }
        latch.countDown();
        for (TestTask<?> task : tasks) {
            task.join();
        }
        forkJoinPool.shutdown();
        return tasks;
    }

    private static String taskName(String name, int index) {
        return name + "[ " + index + " ]";
    }

}
