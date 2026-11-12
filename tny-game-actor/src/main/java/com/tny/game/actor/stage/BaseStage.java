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

package com.tny.game.actor.stage;

import com.tny.game.actor.stage.exception.*;
import com.tny.game.common.utils.*;

import java.util.concurrent.Executor;

/**
 * 基础阶段抽象类
 * Created by Kun Yang on 16/1/22.
 */
@SuppressWarnings("unchecked")
public abstract class BaseStage<R> implements InnerStage<R> {

    private Object name;

    protected InnerStage next;

    private Executor executor;

    protected BaseStage(Object name) {
        this.name = name;
    }

    @Override
    public InnerStage getNext() {
        return next;
    }

    @Override
    public void setNext(InnerStage next) {
        this.next = ObjectAide.as(next);
    }

    @Override
    public Object getName() {
        return name;
    }

    @Override
    public void setSwitchExecutor(Executor executor) {
        this.executor = executor;
    }

    @Override
    public Executor getSwitchExecutor() {
        return this.executor;
    }

    @Override
    public void interrupt() {
        Fragment<Object, ?> fragment = (Fragment<Object, ?>) getFragment();
        if (!fragment.isDone()) {
            fragment.fail(new StageInterruptedException("stage was interrupted"));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void run(Object returnVal, Throwable e) {
        Fragment<Object, ?> fragment = (Fragment<Object, ?>) getFragment();
        if (!fragment.isDone()) {
            fragment.execute(returnVal, e);
        }
    }

}
