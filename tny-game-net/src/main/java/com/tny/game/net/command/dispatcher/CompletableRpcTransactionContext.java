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
import com.tny.game.common.exception.*;
import com.tny.game.common.result.*;
import com.tny.game.net.base.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/12/19 01:40
 **/
abstract class CompletableRpcTransactionContext extends BaseRpcTransactionContext implements RpcEnterCompletable {

    protected final NetMessage message;

    private boolean silently = false;

    CompletableRpcTransactionContext(NetMessage message, boolean async) {
        this(message, async, ContextAttributes.create());
    }

    CompletableRpcTransactionContext(NetMessage message, boolean async, Attributes attributes) {
        super(async, attributes);
        this.message = message;
    }

    @Override
    public MessageSubject getMessageSubject() {
        return message;
    }

    @Override
    public boolean complete(ResultCode code, Throwable error) {
        if (tryCompleted(error)) {
            onComplete(code, null);
            return true;
        }
        return false;
    }

    @Override
    public boolean complete(ResultCode code, Object body, Throwable error) {
        if (tryCompleted(error)) {
            onComplete(code, body);
            return true;
        }
        return false;
    }

    @Override
    public boolean complete(MessageContent content, Throwable error) {
        if (tryCompleted(error)) {
            onComplete(content);
            return true;
        }
        return false;
    }

    @Override
    public boolean completeSilently(Throwable error) {
        if (tryCompleted(error)) {
            silently = true;
            onComplete();
            return true;
        }
        return false;
    }

    @Override
    public boolean completeSilently(ResultCode code, Object body) {
        if (tryCompleted(null)) {
            silently = true;
            onComplete(code, body);
            return true;
        }
        return false;
    }

    @Override
    public boolean complete() {
        return complete(ResultCode.SUCCESS);
    }

    @Override
    protected void onComplete() {
        if (silently) {
            this.onComplete(null);
            return;
        }
        Object body = null;
        ResultCode code = NetResultCode.SERVER_ERROR;
        if (cause instanceof NetException) {
            var exception = (NetException) cause;
            body = exception.getBody();
            code = exception.getCode();
        } else if (cause instanceof ResultCodableException) {
            var exception = (ResultCodableException) cause;
            code = exception.getCode();
        }
        this.onComplete(code, body);
    }

    private void onComplete(ResultCode code, Object body) {
        if (silently) {
            this.onComplete(null);
            return;
        }
        MessageContent content = null;
        switch (message.getMode()) {
            case RESPONSE:
            case PUSH:
                if (body != null) {
                    content = RpcMessageAide.toMessage(message, code, body);
                }
                break;
            case REQUEST:
                content = RpcMessageAide.toMessage(message, code, body);
                break;
        }
        this.onComplete(content);
    }

    private void onComplete(MessageContent content) {
        var cause = getCause();
        if (silently) {
            content = null;
        }
        if (content != null) {
            onReturn(content);
        }
        this.onComplete(content, cause);
    }

    abstract void onComplete(MessageContent result, Throwable exception);

    abstract void onReturn(MessageContent content);

    @Override
    public String toString() {
        return "RpcContext [" + this.message + "]";
    }

}
