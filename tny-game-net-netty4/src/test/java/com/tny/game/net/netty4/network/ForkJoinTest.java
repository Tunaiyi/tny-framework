/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
