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

package com.tny.game.actor.local;

import com.tny.game.actor.*;
import com.tny.game.common.concurrent.*;
import com.tny.game.common.result.*;
import org.slf4j.*;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ActorAnswer<T> extends BaseFuture<T> implements Answer<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActorAnswer.class);

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
