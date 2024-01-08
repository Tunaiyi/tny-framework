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
public class VoidEvent<S> extends ArgsEvent<VoidEventDelegate<S>, S, Void, Void, Void, Void, Void, VoidEvent<S>> {

    public VoidEvent() {
    }

    private VoidEvent(VoidEvent<S> parent) {
        super(parent);
    }

    public VoidEvent(Supplier<Collection<VoidEventDelegate<S>>> factory) {
        super(factory);
    }

    public void notify(S source) {
        doNotify(source, null, null, null, null, null);
    }

    @Override
    public void doParentNotify(VoidEvent<S> parent, S source, Void unused, Void unused2, Void unused3, Void unused4, Void unused5) {
        parent.notify(source);
    }

    @Override
    public void doDelegateNotify(VoidEventDelegate<S> delegate, S source, Void unused, Void unused2, Void unused3, Void unused4, Void unused5) {
        delegate.invoke(source);
    }

    @Override
    public VoidEvent<S> forkChild() {
        return new VoidEvent<>(this);
    }
}