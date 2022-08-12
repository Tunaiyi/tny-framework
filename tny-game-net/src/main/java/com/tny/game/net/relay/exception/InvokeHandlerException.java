/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.relay.exception;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/4/30 4:14 下午
 */
public class InvokeHandlerException extends Exception {

    public InvokeHandlerException() {
    }

    public InvokeHandlerException(String message) {
        super(message);
    }

    public InvokeHandlerException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvokeHandlerException(Throwable cause) {
        super(cause);
    }

    public InvokeHandlerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
