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