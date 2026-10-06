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
public class A2Event<S, A1, A2> extends ArgsEvent<Args2EventDelegate<S, A1, A2>, S, A1, A2, Void, Void, Void, A2Event<S, A1, A2>> {

    public A2Event() {
    }

    private A2Event(A2Event<S, A1, A2> parent) {
        super(parent);
    }

    public A2Event(Supplier<Collection<Args2EventDelegate<S, A1, A2>>> factory) {
        super(factory);
    }

    public void notify(S source, A1 a1, A2 a2) {
        doNotify(source, a1, a2, null, null, null);
    }


    @Override
    public void doParentNotify(A2Event<S, A1, A2> parent, S source, A1 a1, A2 a2, Void unused, Void unused2, Void unused3) {
        parent.notify(source, a1, a2);
    }

    @Override
    public void doDelegateNotify(Args2EventDelegate<S, A1, A2> delegate, S source, A1 a1, A2 a2, Void unused, Void unused2, Void unused3) {
        delegate.invoke(source, a1, a2);
    }

    @Override
    public A2Event<S, A1, A2> forkChild() {
        return new A2Event<>(this);
    }
}