/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

    /**
     * @return 兜底时限剩余毫秒；未配置返回 -1
     */
    public long remainingTimeoutMillis() {
        return this.timeout < 0 ? -1L : Math.max(0L, this.timeout - System.currentTimeMillis());
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