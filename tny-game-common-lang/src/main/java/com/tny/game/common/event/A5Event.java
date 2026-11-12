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
public class A5Event<S, A1, A2, A3, A4, A5>
        extends ArgsEvent<Args5EventDelegate<S, A1, A2, A3, A4, A5>, S, A1, A2, A3, A4, A5, A5Event<S, A1, A2, A3, A4, A5>> {

    public A5Event() {
    }

    private A5Event(A5Event<S, A1, A2, A3, A4, A5> parent) {
        super(parent);
    }

    public A5Event(Supplier<Collection<Args5EventDelegate<S, A1, A2, A3, A4, A5>>> factory) {
        super(factory);
    }

    public void notify(S source, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5) {
        doNotify(source, a1, a2, a3, a4, a5);
    }

    @Override
    public void doParentNotify(A5Event<S, A1, A2, A3, A4, A5> parent, S source, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5) {
        parent.doNotify(source, a1, a2, a3, a4, a5);
    }

    @Override
    public void doDelegateNotify(Args5EventDelegate<S, A1, A2, A3, A4, A5> delegate, S source, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5) {
        delegate.invoke(source, a1, a2, a3, a4, a5);
    }

    @Override
    public A5Event<S, A1, A2, A3, A4, A5> forkChild() {
        return new A5Event<>(this);
    }
}