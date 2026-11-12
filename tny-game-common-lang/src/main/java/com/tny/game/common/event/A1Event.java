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

package com.tny.game.common.event;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Created by Kun Yang on 16/2/4.
 */
public class A1Event<S, A> extends ArgsEvent<Arg1EventDelegate<S, A>, S, A, Void, Void, Void, Void, A1Event<S, A>> {

    public A1Event() {
    }

    private A1Event(A1Event<S, A> parent) {
        super(parent);
    }

    public A1Event(Supplier<Collection<Arg1EventDelegate<S, A>>> factory) {
        super(factory);
    }

    @Override
    public void doParentNotify(A1Event<S, A> parent, S source, A a, Void unused, Void unused2, Void unused3, Void unused4) {
        parent.notify(source, a);
    }

    @Override
    public void doDelegateNotify(Arg1EventDelegate<S, A> delegate, S source, A a, Void unused, Void unused2, Void unused3, Void unused4) {
        delegate.invoke(source, a);
    }

    public void notify(S source, A a) {
        doNotify(source, a, null, null, null, null);
    }

    @Override
    public A1Event<S, A> forkChild() {
        return new A1Event<>(this);
    }
}