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

package com.tny.game.test;

import com.tny.game.common.concurrent.*;

import java.util.*;
import java.util.concurrent.*;

import static com.tny.game.common.utils.ObjectAide.*;

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

    public static TestTask<Void> runnableTask(String name, int times, ThrowableRunnable runnable) {
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

    private TestTask(int times, ThrowableRunnable runnable) {
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
            Object result = TestAide.assertCallComplete(taskName, joinTask::get);
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
