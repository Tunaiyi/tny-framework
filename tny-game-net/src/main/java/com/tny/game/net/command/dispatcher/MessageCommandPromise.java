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

import com.tny.game.common.concurrent.*;
import com.tny.game.net.base.*;
import com.tny.game.net.exception.*;

import java.util.concurrent.Future;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2019-01-23 15:37
 */
public class MessageCommandPromise {

    private final String name;

    private boolean voidable = false;

    private Wait<Object> waiter;

    private Object result;

    private Throwable cause;

    private long timeout = -1;

    private boolean done = false;

    public MessageCommandPromise(String name) {
        this.name = name;
    }

    public boolean isSuccess() {
        return this.done && this.cause == null;
    }

    public boolean isDone() {
        return this.done;
    }

    public boolean isWaiting() {
        return this.waiter != null;
    }

    public Object getResult() {
        return this.result;
    }

    public boolean isVoidable() {
        return voidable;
    }

    public Throwable getCause() {
        return this.cause;
    }

    void checkWait() {
        if (this.waiter != null) { // 检测是否
            if (this.waiter.isDone()) {
                Wait<Object> waiter = this.waiter;
                if (waiter.isSuccess()) {
                    // 等待成功
                    this.setResult(waiter.getResult());
                } else {
                    // 等待异常
                    setResult(waiter.getCause());
                }
            } else {
                if (this.timeout > 0 && System.currentTimeMillis() > this.timeout) {
                    try {
                        throw new RpcInvokeTimeoutException(NetResultCode.EXECUTE_TIMEOUT, "执行 {} 超时", this.name);
                    } catch (Throwable e) {
                        this.setResult(e);
                    }

                }
            }
        }
    }

    public void setVoidResult() {
        this.voidable = true;
        this.waiter = null;
        this.result = null;
        this.done = true;
    }

    public void setResult(Object result) {
        if (result instanceof Future) {
            result = Wait.of(as(result));
        }
        if (result instanceof Wait) {
            this.waiter = as(result);
            this.timeout = System.currentTimeMillis() + 5000; // 超时
        } else if (result instanceof Throwable) {
            this.cause = as(result);
            this.waiter = null;
            this.done = true;
        } else {
            this.result = result;
            this.waiter = null;
            this.done = true;
        }
    }

}