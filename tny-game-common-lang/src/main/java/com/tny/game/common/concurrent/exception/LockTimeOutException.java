/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.common.concurrent.exception;

/**
 * 当锁在release的状态下,会抛出改异常
 *
 * @author KGTny
 */
public class LockTimeOutException extends RuntimeException {

    public LockTimeOutException() {
        super();
    }

    public LockTimeOutException(String message) {
        super(message);
    }

    public LockTimeOutException(String message, Throwable cause) {
        super(message, cause);
    }

    public LockTimeOutException(Throwable cause) {
        super(cause);
    }

}
