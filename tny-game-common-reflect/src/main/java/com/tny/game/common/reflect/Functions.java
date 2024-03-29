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

package com.tny.game.common.reflect;

import org.slf4j.*;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 * Created by Kun Yang on 2016/11/18.
 */
public interface Functions {

    Logger LOGGER = LoggerFactory.getLogger(Functions.class);

    static <T> T exeQuietly(Callable<T> caller, T defValue, Consumer<Throwable> errorHandler) {
        try {
            return caller.call();
        } catch (Throwable e) {
            if (errorHandler != null) {
                errorHandler.accept(e);
            } else {
                LOGGER.error("", e);
            }
        }
        return defValue;
    }

    static <T> T exeQuietly(Callable<T> caller, Consumer<Throwable> errorHandler) {
        return exeQuietly(caller, null, errorHandler);
    }

    static <T> T exeQuietly(Callable<T> caller) {
        return exeQuietly(caller, null, null);
    }

    static void exeQuietly(Runnable runner, Consumer<Throwable> errorHandler) {
        try {
            runner.run();
        } catch (Throwable e) {
            if (errorHandler != null) {
                errorHandler.accept(e);
            } else {
                LOGGER.error("", e);
            }
        }
    }

    static void exeQuietly(Runnable runner) {
        exeQuietly(runner, null);
    }

}
