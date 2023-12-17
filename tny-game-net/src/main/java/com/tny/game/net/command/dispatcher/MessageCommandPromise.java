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
package com.tny.game.net.command.dispatcher;

import com.tny.game.common.utils.*;

import java.util.concurrent.Future;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2019-01-23 15:37
 */
public class MessageCommandPromise {

    private final String name;

    private volatile Object result;

    private volatile Throwable cause;

    private long timeout = -1;

    private boolean done = false;

    public MessageCommandPromise(String name, long timeout) {
        this.name = name;
        if (timeout > 0) {
            this.timeout = System.currentTimeMillis() + timeout;
        }
    }

    public String getName() {
        return name;
    }

    public boolean isTimeout() {
        return System.currentTimeMillis() > this.timeout;
    }

    public boolean isSuccess() {
        return this.done && this.cause == null;
    }

    public boolean isDone() {
        return this.done;
    }

    public Object getResult() {
        return this.result;
    }

    public Throwable getCause() {
        return this.cause;
    }

    public void setResult(Object result) {
        if (result instanceof Future) {
            throw new IllegalArgumentException(StringAide.format("只支持 CompletionStage 的等待, 不支持 Future 类型分返回等待."));
        } else if (result instanceof Throwable) {
            this.cause = as(result);
            this.done = true;
        } else {
            this.result = result;
            this.done = true;
        }
    }

}