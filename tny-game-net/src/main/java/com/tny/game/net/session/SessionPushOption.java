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

package com.tny.game.net.session;

/**
 * Session推送选项
 * Created by Kun Yang on 16/5/17.
 */
public enum SessionPushOption {

    NO_PUSH(false, false),

    PUSH(true, false),

    PUSH_THEN_THROW(false, true),

    //
    ;

    SessionPushOption(boolean push, boolean throwable) {
        this.push = push;
        this.throwable = throwable;
    }

    private boolean push;

    private boolean throwable;

    public boolean isPush() {
        return this.push;
    }

    public boolean isThrowable() {
        return this.throwable;
    }

}
