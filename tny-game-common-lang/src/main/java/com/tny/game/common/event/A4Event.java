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
public class A4Event<S, A1, A2, A3, A4>
        extends ArgsEvent<Args4EventDelegate<S, A1, A2, A3, A4>, S, A1, A2, A3, A4, Void, A4Event<S, A1, A2, A3, A4>> {

    public A4Event() {
    }

    private A4Event(A4Event<S, A1, A2, A3, A4> parent) {
        super(parent);
    }

    public A4Event(Supplier<Collection<Args4EventDelegate<S, A1, A2, A3, A4>>> factory) {
        super(factory);
    }

    public void notify(S source, A1 a1, A2 a2, A3 a3, A4 a4) {
        doNotify(source, a1, a2, a3, a4, null);
    }

    @Override
    public void doParentNotify(A4Event<S, A1, A2, A3, A4> parent, S source, A1 a1, A2 a2, A3 a3, A4 a4, Void unused) {
        parent.notify(source, a1, a2, a3, a4);
    }

    @Override
    public void doDelegateNotify(Args4EventDelegate<S, A1, A2, A3, A4> delegate, S source, A1 a1, A2 a2, A3 a3, A4 a4, Void unused) {
        delegate.invoke(source, a1, a2, a3, a4);
    }

    @Override
    public A4Event<S, A1, A2, A3, A4> forkChild() {
        return new A4Event<>(this);
    }
}