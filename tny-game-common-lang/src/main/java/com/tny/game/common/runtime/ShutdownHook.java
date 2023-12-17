/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
            int value = this.priority = o.priority;
            if (value == 0) {
                return 0;
            }
            return value > 0 ? 1 : -1;
        }

    }

}
