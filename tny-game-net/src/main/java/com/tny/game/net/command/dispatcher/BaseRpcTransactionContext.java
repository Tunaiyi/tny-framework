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
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/12/20 14:43
 **/
public abstract class BaseRpcTransactionContext implements RpcTransactionContext {

    protected Throwable cause;

    private final Attributes attributes;

    private final AtomicBoolean completed = new AtomicBoolean();

    private final AtomicBoolean init = new AtomicBoolean();

    private String operationName;

    private volatile boolean async;

    protected BaseRpcTransactionContext(boolean async) {
        this(async, null);
    }

    protected BaseRpcTransactionContext(boolean async, Attributes attributes) {
        this.async = async;
        this.attributes = attributes == null ? ContextAttributes.create() : attributes;
    }

    @Override
    public Attributes attributes() {
        return attributes;
    }

    @Override
    public String getOperationName() {
        return operationName;
    }

    protected boolean prepare(String operationName) {
        return prepare(operationName, null);
    }

    protected boolean prepare(String operationName, Runnable handle) {
        if (!isValid()) {
            return false;
        }
        if (init.get()) {
            return false;
        }
        if (StringUtils.isBlank(this.operationName)) {
            if (StringUtils.isNotBlank(operationName)) {
                this.operationName = operationName;
            } else if (this.isError()) {
                this.operationName = RpcTransactionContext.errorOperation(this.getMessageSubject());
            }
        }
        if (init.compareAndSet(false, true)) {
            if (handle != null) {
                handle.run();
            }
            this.onPrepare();
            return true;
        }
        return false;
    }

    @Override
    public boolean complete(Throwable error) {
        if (tryCompleted(error)) {
            this.onComplete();
            return true;
        }
        return false;
    }

    protected boolean tryCompleted() {
        return tryCompleted(null);
    }

    protected boolean tryCompleted(Throwable error) {
        if (!isValid()) {
            return false;
        }
        if (completed.compareAndSet(false, true)) {
            this.cause = error;
            this.prepare(null);
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

    @Override
    public boolean isAsync() {
        return async;
    }

    protected abstract void onPrepare();

    protected abstract void onComplete();

    @Override
    public boolean isCompleted() {
        return completed.get();
    }

}
