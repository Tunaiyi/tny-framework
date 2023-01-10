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

import com.tny.game.common.context.*;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/12/20 14:43
 **/
public abstract class BaseRpcInvocationContext implements RpcInvocationContext {

    protected Throwable cause;

    private final Attributes attributes;

    private final AtomicBoolean completed = new AtomicBoolean();

    private final AtomicBoolean prepared = new AtomicBoolean();

    protected BaseRpcInvocationContext() {
        this(null);
    }

    protected BaseRpcInvocationContext(Attributes attributes) {
        this.attributes = attributes == null ? ContextAttributes.create() : attributes;
    }

    @Override
    public Attributes attributes() {
        return attributes;
    }

    @Override
    public boolean prepare() {
        if (prepared.get()) {
            return false;
        }
        if (prepared.compareAndSet(false, true)) {
            this.onPrepare();
            return true;
        }
        return false;
    }

    @Override
    public boolean complete(Throwable error) {
        return tryCompleted(error);
    }

    protected boolean tryCompleted() {
        return tryCompleted(null);
    }

    protected boolean tryCompleted(Throwable error) {
        this.prepare();
        if (completed.compareAndSet(false, true)) {
            this.cause = error;
            onComplete();
            return true;
        }
        return false;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }

    /**
     * @return 是否异常
     */
    @Override
    public boolean isError() {
        return cause != null;
    }

    protected abstract void onPrepare();

    protected abstract void onComplete();

}
