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
package com.tny.game.net.transport;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2019-03-24 03:11
 */
public enum MessageHandleStrategy {

    /**
     * 处理
     */
    HANDLE(true, false),

    /**
     * 忽略
     */
    IGNORE(false, false),

    /**
     * 拦截抛出异常
     */
    THROW(false, true),

    //
    ;

    private final boolean handleable;

    private final boolean throwable;

    MessageHandleStrategy(boolean handleable, boolean throwable) {
        this.handleable = handleable;
        this.throwable = throwable;
    }

    public boolean isHandleable() {
        return handleable;
    }

    public boolean isThrowable() {
        return throwable;
    }
}
