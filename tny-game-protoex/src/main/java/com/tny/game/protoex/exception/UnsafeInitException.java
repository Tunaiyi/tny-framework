/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.protoex.exception;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2020-02-09 00:51
 */
public class UnsafeInitException extends RuntimeException {

    public UnsafeInitException() {
    }

    public UnsafeInitException(String message) {
        super(message);
    }

    public UnsafeInitException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsafeInitException(Throwable cause) {
        super(cause);
    }

    public UnsafeInitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
