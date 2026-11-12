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

package com.tny.game.common.runtime;

import org.slf4j.*;

import java.util.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2018-10-08 14:09
 */
public class ShutdownHook extends Thread {

    public static final Logger LOGGER = LoggerFactory.getLogger(ShutdownHook.class);

    private static final int DEFAULT_PRIORITY = 1000;

    private static ShutdownHook hook;

    private List<CloseResource> holders = new ArrayList<>();

    private ShutdownHook() {
    }

    private static void init() {
        if (hook == null) {
            hook = new ShutdownHook();
            LOGGER.info("ShutDownHook is initialized");
        }
    }

    @Override
    public void run() {
        closeAll();
    }

    public static void runHook(boolean sync) {
        if (hook != null) {
            if (sync) {
                hook.run();
            } else {
                hook.start();
            }
        }
    }

    private synchronized void closeAll() {
        Collections.sort(holders);
        LOGGER.info("Begin shutdown all");
        for (CloseResource resource : holders) {
            try {
                Closeable shutdownable = resource.closeable;
                LOGGER.info("Start shutdown {} [{}]", resource.getClass(), resource.priority);
                shutdownable.close();
                LOGGER.info("End shutdown {} [{}]", resource.getClass(), resource.priority);
            } catch (Exception e) {
                LOGGER.info("Shutdown {} [{}] exception", resource.getClass(), resource.priority, e);
            }
        }
        LOGGER.info("Finish to Shutdown all");
        holders.clear();
    }

    public static synchronized void register(Closeable closeable) {
        register(closeable, DEFAULT_PRIORITY);
    }

    public static synchronized void register(Closeable closeable, int priority) {
        if (hook == null) {
            init();
        }
        hook.holders.add(new CloseResource(closeable, priority));
        LOGGER.info("register Closeable {}, priority : {}", closeable.getClass(), priority);
    }

    private static class CloseResource implements Comparable<CloseResource> {

        private Closeable closeable;

        private int priority;

        public CloseResource(Closeable closeable, int priority) {
            this.closeable = closeable;
            this.priority = priority;
        }

        @Override
        public int compareTo(CloseResource o) {
            // 原实现 this.priority = o.priority：比较变互相赋值，排序契约尽毁
            return Integer.compare(this.priority, o.priority);
        }

    }

}
