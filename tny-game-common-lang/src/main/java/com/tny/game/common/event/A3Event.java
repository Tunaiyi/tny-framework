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
public class A3Event<S, A1, A2, A3> extends ArgsEvent<Args3EventDelegate<S, A1, A2, A3>, S, A1, A2, A3, Void, Void, A3Event<S, A1, A2, A3>> {

    public A3Event() {
    }

    private A3Event(A3Event<S, A1, A2, A3> parent) {
        super(parent);
    }

    public A3Event(Supplier<Collection<Args3EventDelegate<S, A1, A2, A3>>> factory) {
        super(factory);
    }

    public void notify(S source, A1 a1, A2 a2, A3 a3) {
        doNotify(source, a1, a2, a3, null, null);
    }

    @Override
    public void doParentNotify(A3Event<S, A1, A2, A3> parent, S source, A1 a1, A2 a2, A3 a3, Void unused, Void unused2) {
        parent.notify(source, a1, a2, a3);
    }

    @Override
    public void doDelegateNotify(Args3EventDelegate<S, A1, A2, A3> delegate, S source, A1 a1, A2 a2, A3 a3, Void unused, Void unused2) {
        delegate.invoke(source, a1, a2, a3);
    }

    @Override
    public A3Event<S, A1, A2, A3> forkChild() {
        return new A3Event<>(this);
    }
}