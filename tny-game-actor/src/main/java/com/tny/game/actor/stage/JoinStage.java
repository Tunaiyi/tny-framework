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

import com.tny.game.actor.stage.Stages.*;

/**
 * Created by Kun Yang on 16/1/22.
 */
class JoinStage<T> extends BaseStage<Stage<T>> {

    protected JoinFragment<?, ?, T> targetFragment;

    @SuppressWarnings("unchecked")
    public JoinStage(Object name, JoinFragment<?, ?, T> fragment) {
        super(name);
        this.targetFragment = fragment;
    }

    // @SuppressWarnings("unchecked")
    // public JoinStage(CommonStage head, JoinSupplierDoneSupplierFragment<T> fragment) {
    //     super(head);
    //     this.targetFragment = fragment;
    // }

    @Override
    public boolean isNoneParam() {
        return false;
    }

    @Override
    public Fragment<?, Stage<T>> getFragment() {
        return this.targetFragment;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void run(Object returnVal, Throwable e) {
        if (!this.targetFragment.isDone()) {
            targetFragment.execute(returnVal, e);
            if (!targetFragment.isDone()) {
                return;
            }
            InnerStage stage = (InnerStage) targetFragment.getStage();
            stage.setNext(this.next);
            this.setNext(stage);
        }
    }

}
