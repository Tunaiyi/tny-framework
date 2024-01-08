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