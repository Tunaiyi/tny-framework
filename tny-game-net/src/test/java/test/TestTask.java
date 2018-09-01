package test;

import com.tny.game.common.concurrent.RunnableWithThrow;

import java.util.*;
import java.util.concurrent.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static test.TestAide.*;

/**
 * Created by Kun Yang on 2018/8/27.
 */
public class TestTask<T> {

    private int times;

    private String name;

    private Callable<T> callable;

    private List<T> results = new ArrayList<>();

    private List<ForkJoinTask<T>> joinTasks = new ArrayList<>();

    public static <T> TestTask<T> callableTask(String name, int times, Callable<T> callable) {
        return new TestTask<T>(name, times, callable);
    }

    public static TestTask<Void> runnableTask(String name, int times, RunnableWithThrow runnable) {
        return new TestTask<>(name, times, () -> {
            runnable.run();
            return null;
        });
    }

    private TestTask(String name, int times, Callable<T> callable) {
        this.times = times;
        this.name = name;
        this.callable = callable;
    }

    private TestTask(int times, RunnableWithThrow runnable) {
        this.times = times;
        this.callable = () -> {
            runnable.run();
            return null;
        };
    }

    public int getTimes() {
        return times;
    }

    public Callable<T> getCallable() {
        return callable;
    }

    public List<T> getResults() {
        return Collections.unmodifiableList(results);
    }

    void addTask(ForkJoinTask<?> joinTask) {
        this.joinTasks.add(as(joinTask));
    }

    void join() {
        int index = 0;
        for (ForkJoinTask<?> joinTask : joinTasks) {
            String taskName = taskName(name, index++);
            Object result = assertCallWithoutException(taskName, joinTask::get);
            results.add(as(result));
        }
    }

    private static String taskName(String name, int index) {
        return name + "[ " + index + " ]";
    }

    public String getName() {
        return name;
    }
}
