/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.netty4.network;

import org.slf4j.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2018-10-24 19:07
 */
public class ForkJoinTest {

    public static final Logger LOGGER = LoggerFactory.getLogger(ForkJoinTest.class);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ForkJoinPool pool = new ForkJoinPool(4);

        System.out.println(pool.submit(new RecursiveTask<Integer>() {

            @Override
            protected Integer compute() {
                LOGGER.info("start submit | {}", Thread.currentThread().getName());
                RecursiveTask<Integer> task = new RecursiveTask<Integer>() {

                    @Override
                    protected Integer compute() {
                        LOGGER.info("start run task | {}", Thread.currentThread().getName());
                        List<RecursiveTask<Integer>> subTasks = new ArrayList<>();
                        for (int index = 0; index < 100; index++) {
                            int id = index;
                            subTasks.add(new RecursiveTask<Integer>() {

                                @Override
                                protected Integer compute() {
                                    int step;
                                    for (step = 0; step < 10; step++) {
                                        LOGGER.info("id {} | step {} | {}",
                                                id, step, Thread.currentThread().getName());
                                        try {
                                            Thread.sleep(1000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    return step;
                                }

                            });
                        }
                        LOGGER.info("start invoke subTask | {}", Thread.currentThread().getName());
                        invokeAll(subTasks);
                        int value = 0;
                        for (RecursiveTask<Integer> subTask : subTasks)
                            value += subTask.getRawResult();
                        LOGGER.info("start invoke subTask finish | {}", Thread.currentThread().getName());
                        return value;
                    }

                };
                invokeAll(task);
                return task.getRawResult();
            }
        }).get());
    }

}
