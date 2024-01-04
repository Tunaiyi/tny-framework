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

package com.tny.game.actor.local;

import com.tny.game.actor.*;
import com.tny.game.common.concurrent.*;
import com.tny.game.common.result.*;
import org.slf4j.*;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

class ActorAnswer<T> extends BaseFuture<T> implements Answer<T> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ActorAnswer.class);

    private volatile List<AnswerListener<T>> listeners;

    @Override
    public Done<T> getDone() {
        if (!this.isDone()) {
            return DoneResults.failure();
        }
        return DoneResults.successNullable(this.getRawValue());
    }

    protected boolean success(T value) {
        if (super.set(value)) {
            fire();
            return true;
        }
        return false;
    }

    protected boolean fail(Throwable cause) {
        if (super.setFailure(cause)) {
            fire();
            return true;
        }
        return false;
    }

    @Override
    public Throwable getCause() {
        return this.getRawCause();
    }

    @Override
    public boolean isFail() {
        return isDone() && this.getRawCause() != null;
    }

    @Override
    public void addListener(AnswerListener<T> listener) {
        if (this.listeners == null) {
            this.listeners = new CopyOnWriteArrayList<>();
        }
        this.listeners.add(listener);
    }

    private void fire() {
        if (this.listeners == null) {
            return;
        }
        this.listeners.forEach(l -> {
            try {
                l.onDone(this, getRawValue(), getRawCause(), isCancelled());
            } catch (Throwable e) {
                LOGGER.error("{}.done 异常", l.getClass(), e);
            }
        });
    }

}
