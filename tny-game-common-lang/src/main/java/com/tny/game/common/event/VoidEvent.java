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