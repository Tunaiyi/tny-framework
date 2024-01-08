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