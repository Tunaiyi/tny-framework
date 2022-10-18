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
package com.tny.game.common.concurrent.exception;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/9/27 15:12
 **/
public class WorkerExecuteTimeoutException extends RuntimeException {

    public WorkerExecuteTimeoutException() {
    }

    public WorkerExecuteTimeoutException(String message) {
        super(message);
    }

    public WorkerExecuteTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public WorkerExecuteTimeoutException(Throwable cause) {
        super(cause);
    }

    public WorkerExecuteTimeoutException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
