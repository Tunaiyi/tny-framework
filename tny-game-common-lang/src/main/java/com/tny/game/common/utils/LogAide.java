/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.common.utils;

import org.slf4j.*;

public class LogAide {

    public static final Logger LOGGER = LoggerFactory.getLogger(LogAide.class);

    public static final String LOADER = "configLoader";

    public static final String EVENT = "commonEvent";

    public static final String LOCK = "commonLock";

    public static final String FILE_MONITOR = "fileMonitor";

    public static final String TIME_TASK = "timeTask";

    public static final String WORKER = "worker";

    public static Object[] msg(Object... objects) {
        return objects;
    }

    public static byte trace(byte value) {
        return trace(LOGGER, null, value);
    }

    public static byte trace(Logger logger, byte value) {
        return trace(logger, null, value);
    }

    public static byte trace(Object key, byte value) {
        return trace(LOGGER, key, value);
    }

    public static byte trace(Logger logger, Object key, byte value) {
        if (logger.isDebugEnabled()) {
            logger.debug(key == null ? "{}" : key + " = {}", value);
        }
        return value;
    }

    public static short trace(short value) {
        return trace(LOGGER, null, value);
    }

    public static short trace(Logger logger, short value) {
        return trace(logger, null, value);
    }

    public static short trace(Object key, short value) {
        return trace(LOGGER, key, value);
    }

    public static short trace(Logger logger, Object key, short value) {
        if (logger.isDebugEnabled()) {
            logger.debug(key == null ? "{}" : key + " = {}", value);
        }
        return value;
    }

    public static int trace(int value) {
        return trace(LOGGER, null, value);
    }

    public static int trace(Logger logger, int value) {
        return trace(logger, null, value);
    }

    public static int trace(Object key, int value) {
        return trace(LOGGER, key, value);
    }

    public static int trace(Logger logger, Object key, int value) {
        if (logger.isDebugEnabled()) {
            logger.debug(key == null ? "{}" : key + " = {}", value);
        }
        return value;
    }

    public static long trace(long value) {
        return trace(LOGGER, null, value);
    }

    public static long trace(Logger logger, long value) {
        return trace(logger, null, value);
    }

    public static long trace(Object key, long value) {
        return trace(LOGGER, key, value);
    }

    public static long trace(Logger logger, Object key, long value) {
        if (logger.isDebugEnabled()) {
            logger.debug(key == null ? "{}" : key + " = {}", value);
        }
        return value;
    }

    public static float trace(float value) {
        return trace(LOGGER, null, value);
    }

    public static float trace(Logger logger, float value) {
        return trace(logger, null, value);
    }

    public static float trace(Object key, float value) {
        return trace(LOGGER, key, value);
    }

    public static float trace(Logger logger, Object key, float value) {
        if (logger.isDebugEnabled()) {
            logger.debug(key == null ? "{}" : key + " = {}", value);
        }
        return value;
    }

    public static double trace(double value) {
        return trace(LOGGER, null, value);
    }

    public static double trace(Logger logger, double value) {
        return trace(logger, null, value);
    }

    public static double trace(Object key, double value) {
        return trace(LOGGER, key, value);
    }

    public static double trace(Logger logger, Object key, double value) {
        if (logger.isDebugEnabled()) {
            logger.debug(key == null ? "{}" : key + " = {}", value);
        }
        return value;
    }

    public static boolean trace(boolean value) {
        return trace(LOGGER, null, value);
    }

    public static boolean trace(Logger logger, boolean value) {
        return trace(logger, null, value);
    }

    public static boolean trace(Object key, boolean value) {
        return trace(LOGGER, key, value);
    }

    public static boolean trace(Logger logger, Object key, boolean value) {
        if (logger.isDebugEnabled()) {
            logger.debug(key == null ? "{}" : key + " = {}", value);
        }
        return value;
    }

    public static <T> T trace(T value) {
        return trace(LOGGER, null, value);
    }

    public static <T> T trace(Logger logger, T value) {
        return trace(logger, null, value);
    }

    public static <T> T trace(Object key, T value) {
        return trace(LOGGER, key, value);
    }

    public static <T> T trace(Logger logger, Object key, T value) {
        if (logger.isDebugEnabled()) {
            logger.debug(key == null ? "{}" : key + " = {}", value);
        }
        return value;
    }

}
